package com.project.covid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.model.CovidData;
import com.project.covid.model.CustomAnalysis;
import com.project.covid.repository.CovidDataRepository;
import com.project.covid.repository.CustomAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomAnalysisService {
    
    @Autowired
    private CovidDataRepository covidDataRepository;
    
    @Autowired
    private CustomAnalysisRepository customAnalysisRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Transactional
    public CustomAnalysis saveCustomAnalysis(String state, LocalDate startDate, LocalDate endDate, 
                                            List<String> metrics, String title, String description) {
        
        // Generate analysis result
        AnalysisResult analysisResult = generateCustomAnalysis(state, startDate, endDate, metrics);
        
        // Generate chart data
        ChartData chartData = generateCustomChartData(state, startDate, endDate, metrics);
        
        // Create and save custom analysis entity
        CustomAnalysis customAnalysis = new CustomAnalysis();
        customAnalysis.setState(state);
        customAnalysis.setStartDate(startDate);
        customAnalysis.setEndDate(endDate);
        customAnalysis.setMetrics(String.join(",", metrics));
        customAnalysis.setTitle(title != null ? title : "Custom Analysis");
        customAnalysis.setDescription(description);
        
        try {
            customAnalysis.setAnalysisData(objectMapper.writeValueAsString(analysisResult));
            customAnalysis.setChartData(objectMapper.writeValueAsString(chartData));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize analysis data", e);
        }
        
        // Save to database and return the saved entity
        System.out.println("Saving custom analysis to database: " + customAnalysis.getTitle());
        CustomAnalysis savedAnalysis = customAnalysisRepository.save(customAnalysis);
        System.out.println("Custom analysis saved with ID: " + savedAnalysis.getId());
        return savedAnalysis;
    }
    
    public List<CustomAnalysis> getAllCustomAnalyses() {
        return customAnalysisRepository.findAllByOrderByCreatedAtDesc();
    }
    
    public List<CustomAnalysis> getCustomAnalysesByState(String state) {
        return customAnalysisRepository.findByStateOrderByCreatedAtDesc(state);
    }
    
    public CustomAnalysis getCustomAnalysisById(Long id) {
        return customAnalysisRepository.findById(id).orElse(null);
    }
    
    public AnalysisResult getAnalysisResultFromCustomAnalysis(CustomAnalysis customAnalysis) {
        try {
            return objectMapper.readValue(customAnalysis.getAnalysisData(), AnalysisResult.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize analysis data", e);
        }
    }
    
    public ChartData getChartDataFromCustomAnalysis(CustomAnalysis customAnalysis) {
        try {
            return objectMapper.readValue(customAnalysis.getChartData(), ChartData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize chart data", e);
        }
    }
    
    private AnalysisResult generateCustomAnalysis(String state, LocalDate startDate, LocalDate endDate, List<String> metrics) {
        List<CovidData> filteredData;
        
        // Filter data based on parameters
        if (state != null && !state.isEmpty()) {
            if (startDate != null && endDate != null) {
                filteredData = covidDataRepository.findByStateAndDateBetween(state, startDate, endDate);
            } else {
                filteredData = covidDataRepository.findByState(state);
            }
        } else if (startDate != null && endDate != null) {
            filteredData = covidDataRepository.findByDateBetween(startDate, endDate);
        } else {
            filteredData = covidDataRepository.findAll();
        }
        
        // Generate analysis result
        Map<String, Object> resultData = new HashMap<>();
        
        // Basic statistics
        int totalConfirmed = 0;
        int totalDeaths = 0;
        int totalCured = 0;
        
        for (CovidData data : filteredData) {
            totalConfirmed += data.getConfirmed();
            totalDeaths += data.getDeaths();
            totalCured += data.getCured();
        }
        
        resultData.put("totalConfirmed", totalConfirmed);
        resultData.put("totalDeaths", totalDeaths);
        resultData.put("totalCured", totalCured);
        
        // Calculate rates
        if (totalConfirmed > 0) {
            double mortalityRate = (double) totalDeaths / totalConfirmed * 100;
            double recoveryRate = (double) totalCured / totalConfirmed * 100;
            
            resultData.put("mortalityRate", mortalityRate);
            resultData.put("recoveryRate", recoveryRate);
        }
        
        // Find highest cases day
        if (!filteredData.isEmpty()) {
            CovidData highestCasesDay = filteredData.stream()
                    .max((a, b) -> Integer.compare(a.getConfirmed(), b.getConfirmed()))
                    .orElse(null);
            
            if (highestCasesDay != null) {
                resultData.put("dateWithHighestCases", highestCasesDay.getDate());
                resultData.put("highestCasesCount", highestCasesDay.getConfirmed());
            }
            
            // Add date range info
            resultData.put("startDate", filteredData.stream()
                    .min((a, b) -> a.getDate().compareTo(b.getDate()))
                    .map(CovidData::getDate)
                    .orElse(null));
            
            resultData.put("endDate", filteredData.stream()
                    .max((a, b) -> a.getDate().compareTo(b.getDate()))
                    .map(CovidData::getDate)
                    .orElse(null));
        }
        
        // Add filter parameters to result
        resultData.put("stateFilter", state);
        resultData.put("startDateFilter", startDate);
        resultData.put("endDateFilter", endDate);
        resultData.put("metricsFilter", metrics);
        
        String title = "Custom Analysis";
        if (state != null && !state.isEmpty()) {
            title += " for " + state;
        }
        if (startDate != null && endDate != null) {
            title += " from " + startDate + " to " + endDate;
        }
        
        return new AnalysisResult(title, resultData);
    }
    
    private ChartData generateCustomChartData(String state, LocalDate startDate, LocalDate endDate, List<String> metrics) {
        List<CovidData> filteredData;
        
        // Filter data based on parameters
        if (state != null && !state.isEmpty()) {
            if (startDate != null && endDate != null) {
                filteredData = covidDataRepository.findByStateAndDateBetween(state, startDate, endDate);
            } else {
                filteredData = covidDataRepository.findByState(state);
            }
        } else if (startDate != null && endDate != null) {
            filteredData = covidDataRepository.findByDateBetween(startDate, endDate);
        } else {
            filteredData = covidDataRepository.findAll();
        }
        
        // Sort data by date
        filteredData = filteredData.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
        
        // Prepare chart data
        List<String> labels = filteredData.stream()
                .map(data -> data.getDate().toString())
                .collect(Collectors.toList());
        
        Map<String, List<Number>> datasets = new HashMap<>();
        
        if (metrics.contains("confirmed")) {
            List<Number> confirmedCases = filteredData.stream()
                    .map(CovidData::getConfirmed)
                    .collect(Collectors.toList());
            datasets.put("Confirmed Cases", confirmedCases);
        }
        
        if (metrics.contains("deaths")) {
            List<Number> deaths = filteredData.stream()
                    .map(CovidData::getDeaths)
                    .collect(Collectors.toList());
            datasets.put("Deaths", deaths);
        }
        
        if (metrics.contains("recovered")) {
            List<Number> cured = filteredData.stream()
                    .map(CovidData::getCured)
                    .collect(Collectors.toList());
            datasets.put("Cured", cured);
        }
        
        String title = "Custom Analysis";
        if (state != null && !state.isEmpty()) {
            title += " for " + state;
        }
        if (startDate != null && endDate != null) {
            title += " from " + startDate + " to " + endDate;
        }
        
        return new ChartData("line", title, "Date", "Number of Cases", labels, datasets);
    }
}
