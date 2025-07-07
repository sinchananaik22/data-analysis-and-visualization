package com.project.covid.service;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.model.CovidData;
import com.project.covid.pattern.observer.DataChangeObserver;
import com.project.covid.pattern.strategy.AnalysisStrategy;
import com.project.covid.repository.CovidDataRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CovidDataService {
    
    @Autowired
    private CovidDataRepository covidDataRepository;
    
    private List<DataChangeObserver> observers = new ArrayList<>();
    
    // Method to register observers (Observer Pattern)
    public void registerObserver(DataChangeObserver observer) {
        observers.add(observer);
    }
    
    // Method to notify observers when data changes
    private void notifyObservers() {
        for (DataChangeObserver observer : observers) {
            observer.onDataChange();
        }
    }
    
    // Method to parse and save CSV data
    public List<CovidData> processAndSaveCSV(MultipartFile file) throws IOException {
        List<CovidData> covidDataList = new ArrayList<>();
        
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader()
                     .withIgnoreHeaderCase().withTrim())) {
            
            for (CSVRecord csvRecord : csvParser) {
                // Get the header map to find the correct column names
                Map<String, Integer> headerMap = csvParser.getHeaderMap();
                
                // Find the correct column names (handling potential BOM character)
                String snoColumn = findColumn(headerMap, "Sno");
                String dateColumn = findColumn(headerMap, "Date");
                String timeColumn = findColumn(headerMap, "Time");
                String stateColumn = findColumn(headerMap, "State/UnionTerritory");
                String confirmedIndianColumn = findColumn(headerMap, "ConfirmedIndianNational");
                String confirmedForeignColumn = findColumn(headerMap, "ConfirmedForeignNational");
                String curedColumn = findColumn(headerMap, "Cured");
                String deathsColumn = findColumn(headerMap, "Deaths");
                String confirmedColumn = findColumn(headerMap, "Confirmed");
                
                CovidData covidData = new CovidData(
                        csvRecord.get(snoColumn),
                        csvRecord.get(dateColumn),
                        csvRecord.get(timeColumn),
                        csvRecord.get(stateColumn),
                        csvRecord.get(confirmedIndianColumn),
                        csvRecord.get(confirmedForeignColumn),
                        csvRecord.get(curedColumn),
                        csvRecord.get(deathsColumn),
                        csvRecord.get(confirmedColumn)
                );
                covidDataList.add(covidData);
            }
            
            // Save all data to the repository
            covidDataRepository.saveAll(covidDataList);
            
            // Notify observers that data has changed
            notifyObservers();
            
            return covidDataList;
        }
    }

    // Helper method to find column name regardless of BOM characters
    private String findColumn(Map<String, Integer> headerMap, String columnName) {
        // Try exact match first
        if (headerMap.containsKey(columnName)) {
            return columnName;
        }

        // Try case-insensitive match
        for (String header : headerMap.keySet()) {
            if (header.trim().equalsIgnoreCase(columnName)) {
                return header;
            }
        }

        // Try to find a header that ends with the column name (to handle BOM characters)
        for (String header : headerMap.keySet()) {
            if (header.trim().endsWith(columnName)) {
                return header;
            }
        }

        throw new IllegalArgumentException("Column '" + columnName + "' not found in CSV. Available columns: " + headerMap.keySet());
    }
    
    // Method to analyze data using a specific strategy (Strategy Pattern)
    public AnalysisResult analyzeData(AnalysisStrategy strategy) {
        return strategy.analyze(covidDataRepository);
    }
    
    // Method to get all COVID data
    public List<CovidData> getAllCovidData() {
        return covidDataRepository.findAll();
    }
    
    // Method to get data for a specific state
    public List<CovidData> getDataByState(String state) {
        return covidDataRepository.findByState(state);
    }
    
    // Method to get data between two dates
    public List<CovidData> getDataByDateRange(LocalDate startDate, LocalDate endDate) {
        return covidDataRepository.findByDateBetween(startDate, endDate);
    }
    
    // Method to get statewise aggregated data
    public List<Object[]> getStatewiseAggregatedData() {
        return covidDataRepository.getStatewiseAggregatedData();
    }
    
    // Method to get datewise aggregated data
    public List<Object[]> getDatewiseAggregatedData() {
        return covidDataRepository.getDatewiseAggregatedData();
    }
    
    // Method to get all states
    public List<String> getAllStates() {
        return covidDataRepository.getAllStates();
    }
    
    // Method to prepare chart data for state comparison
    public ChartData prepareStateComparisonChartData() {
        List<Object[]> stateData = covidDataRepository.getStatewiseAggregatedData();
        
        // Limit to top 10 states for better visualization
        int limit = Math.min(stateData.size(), 10);
        
        List<String> labels = new ArrayList<>();
        List<Number> confirmedCases = new ArrayList<>();
        List<Number> deaths = new ArrayList<>();
        List<Number> cured = new ArrayList<>();
        
        for (int i = 0; i < limit; i++) {
            Object[] data = stateData.get(i);
            labels.add((String) data[0]);
            confirmedCases.add((Number) data[1]);
            deaths.add((Number) data[2]);
            cured.add((Number) data[3]);
        }
        
        Map<String, List<Number>> datasets = new HashMap<>();
        datasets.put("Confirmed Cases", confirmedCases);
        datasets.put("Deaths", deaths);
        datasets.put("Cured", cured);
        
        return new ChartData("bar", "COVID-19 Cases by State (Top 10)", 
                "States", "Number of Cases", labels, datasets);
    }
    
    // Method to prepare chart data for time series analysis
    public ChartData prepareTimeSeriesChartData() {
        List<Object[]> dateData = covidDataRepository.getDatewiseAggregatedData();
        
        List<String> labels = new ArrayList<>();
        List<Number> confirmedCases = new ArrayList<>();
        List<Number> deaths = new ArrayList<>();
        List<Number> cured = new ArrayList<>();
        
        for (Object[] data : dateData) {
            LocalDate date = (LocalDate) data[0];
            labels.add(date.toString());
            confirmedCases.add((Number) data[1]);
            deaths.add((Number) data[2]);
            cured.add((Number) data[3]);
        }
        
        Map<String, List<Number>> datasets = new HashMap<>();
        datasets.put("Confirmed Cases", confirmedCases);
        datasets.put("Deaths", deaths);
        datasets.put("Cured", cured);
        
        return new ChartData("line", "COVID-19 Cases Over Time", 
                "Date", "Number of Cases", labels, datasets);
    }
    
    // Method to prepare chart data for a specific state
    public ChartData prepareStateTimeSeriesChartData(String state) {
        List<CovidData> stateData = covidDataRepository.getStateDataChronologically(state);
        
        List<String> labels = new ArrayList<>();
        List<Number> confirmedCases = new ArrayList<>();
        List<Number> deaths = new ArrayList<>();
        List<Number> cured = new ArrayList<>();
        
        for (CovidData data : stateData) {
            labels.add(data.getDate().toString());
            confirmedCases.add(data.getConfirmed());
            deaths.add(data.getDeaths());
            cured.add(data.getCured());
        }
        
        Map<String, List<Number>> datasets = new HashMap<>();
        datasets.put("Confirmed Cases", confirmedCases);
        datasets.put("Deaths", deaths);
        datasets.put("Cured", cured);
        
        return new ChartData("line", "COVID-19 Cases in " + state, 
                "Date", "Number of Cases", labels, datasets);
    }
}
