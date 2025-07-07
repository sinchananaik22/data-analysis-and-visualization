package com.project.covid.pattern.prototype;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.pattern.builder.AnalysisResultBuilder;
import com.project.covid.pattern.builder.ChartDataBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Prototype Pattern: Concrete implementation of AnalysisTemplate for time series analysis
 */
public class TimeSeriesAnalysisTemplate extends AnalysisTemplate {
    
    public TimeSeriesAnalysisTemplate() {
        super("Time Series Analysis", "Analyzes COVID-19 data over time");
        defaultParameters.put("calculateDailyChanges", true);
        defaultParameters.put("calculateGrowthRates", true);
    }
    
    @Override
    public AnalysisResult generateAnalysis(Map<String, Object> parameters) {
        // Merge with default parameters
        Map<String, Object> mergedParams = new HashMap<>(defaultParameters);
        if (parameters != null) {
            mergedParams.putAll(parameters);
        }
        
        // Get data from parameters
        @SuppressWarnings("unchecked")
        List<Object[]> datewiseData = (List<Object[]>) mergedParams.get("datewiseData");
        
        if (datewiseData == null || datewiseData.isEmpty()) {
            return new AnalysisResultBuilder()
                    .analysisType("Time Series Analysis")
                    .addData("error", "No data available")
                    .build();
        }
        
        // Build analysis result
        AnalysisResultBuilder builder = new AnalysisResultBuilder()
                .analysisType("Time Series Analysis");
        
        // Find date with highest new cases
        LocalDate dateWithHighestNewCases = null;
        long highestNewCases = 0;
        long previousDayConfirmed = 0;
        
        // Find date with highest new deaths
        LocalDate dateWithHighestNewDeaths = null;
        long highestNewDeaths = 0;
        long previousDayDeaths = 0;
        
        // Find date with highest new recoveries
        LocalDate dateWithHighestNewRecoveries = null;
        long highestNewRecoveries = 0;
        long previousDayCured = 0;
        
        for (int i = 0; i < datewiseData.size(); i++) {
            Object[] data = datewiseData.get(i);
            LocalDate date = (LocalDate) data[0];
            long confirmed = ((Number) data[1]).longValue();
            long deaths = ((Number) data[2]).longValue();
            long cured = ((Number) data[3]).longValue();
            
            // Calculate daily changes if requested
            if ((Boolean) mergedParams.getOrDefault("calculateDailyChanges", true) && i > 0) {
                long newCases = confirmed - previousDayConfirmed;
                long newDeaths = deaths - previousDayDeaths;
                long newRecoveries = cured - previousDayCured;
                
                if (newCases > highestNewCases) {
                    highestNewCases = newCases;
                    dateWithHighestNewCases = date;
                }
                
                if (newDeaths > highestNewDeaths) {
                    highestNewDeaths = newDeaths;
                    dateWithHighestNewDeaths = date;
                }
                
                if (newRecoveries > highestNewRecoveries) {
                    highestNewRecoveries = newRecoveries;
                    dateWithHighestNewRecoveries = date;
                }
                
                // Add daily changes to result
                builder.addData(date + "_newCases", newCases);
                builder.addData(date + "_newDeaths", newDeaths);
                builder.addData(date + "_newRecoveries", newRecoveries);
            }
            
            // Add cumulative data
            builder.addData(date + "_confirmed", confirmed);
            builder.addData(date + "_deaths", deaths);
            builder.addData(date + "_cured", cured);
            
            // Update previous day values
            previousDayConfirmed = confirmed;
            previousDayDeaths = deaths;
            previousDayCured = cured;
        }
        
        // Add summary statistics
        if (dateWithHighestNewCases != null) {
            builder.addData("dateWithHighestNewCases", dateWithHighestNewCases);
            builder.addData("highestNewCases", highestNewCases);
        }
        
        if (dateWithHighestNewDeaths != null) {
            builder.addData("dateWithHighestNewDeaths", dateWithHighestNewDeaths);
            builder.addData("highestNewDeaths", highestNewDeaths);
        }
        
        if (dateWithHighestNewRecoveries != null) {
            builder.addData("dateWithHighestNewRecoveries", dateWithHighestNewRecoveries);
            builder.addData("highestNewRecoveries", highestNewRecoveries);
        }
        
        // Calculate growth rates if requested
        if ((Boolean) mergedParams.getOrDefault("calculateGrowthRates", true) && datewiseData.size() > 1) {
            Object[] firstDay = datewiseData.get(0);
            Object[] lastDay = datewiseData.get(datewiseData.size() - 1);
            
            long firstDayConfirmed = ((Number) firstDay[1]).longValue();
            long lastDayConfirmed = ((Number) lastDay[1]).longValue();
            
            if (firstDayConfirmed > 0) {
                double growthRate = (double) (lastDayConfirmed - firstDayConfirmed) / firstDayConfirmed * 100;
                builder.addData("overallGrowthRate", growthRate);
            }
        }
        
        return builder.build();
    }
    
    @Override
    public ChartData generateChartData(Map<String, Object> parameters) {
        // Merge with default parameters
        Map<String, Object> mergedParams = new HashMap<>(defaultParameters);
        if (parameters != null) {
            mergedParams.putAll(parameters);
        }
        
        // Get data from parameters
        @SuppressWarnings("unchecked")
        List<Object[]> datewiseData = (List<Object[]>) mergedParams.get("datewiseData");
        
        if (datewiseData == null || datewiseData.isEmpty()) {
            return new ChartDataBuilder()
                    .chartType("line")
                    .title("No Data Available")
                    .build();
        }
        
        // Prepare chart data
        List<String> labels = new ArrayList<>();
        List<Number> confirmedCases = new ArrayList<>();
        List<Number> deaths = new ArrayList<>();
        List<Number> cured = new ArrayList<>();
        
        for (Object[] data : datewiseData) {
            LocalDate date = (LocalDate) data[0];
            labels.add(date.toString());
            confirmedCases.add((Number) data[1]);
            deaths.add((Number) data[2]);
            cured.add((Number) data[3]);
        }
        
        // Build chart data
        ChartDataBuilder builder = new ChartDataBuilder()
                .chartType("line")
                .title("COVID-19 Cases Over Time")
                .xAxisLabel("Date")
                .yAxisLabel("Number of Cases")
                .labels(labels)
                .dataset("Confirmed Cases", confirmedCases)
                .dataset("Deaths", deaths)
                .dataset("Cured", cured);
        
        return builder.build();
    }
}
