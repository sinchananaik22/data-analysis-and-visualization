package com.project.covid.pattern.prototype;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.model.CovidData;
import com.project.covid.pattern.builder.AnalysisResultBuilder;
import com.project.covid.pattern.builder.ChartDataBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Prototype Pattern: Concrete implementation of AnalysisTemplate for statewise analysis
 */
public class StatewiseAnalysisTemplate extends AnalysisTemplate {
    
    public StatewiseAnalysisTemplate() {
        super("Statewise Analysis", "Analyzes COVID-19 data by state");
        defaultParameters.put("includeRecoveryRate", true);
        defaultParameters.put("includeMortalityRate", true);
        defaultParameters.put("topStatesCount", 10);
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
        List<Object[]> statewiseData = (List<Object[]>) mergedParams.get("statewiseData");
        
        if (statewiseData == null || statewiseData.isEmpty()) {
            return new AnalysisResultBuilder()
                    .analysisType("Statewise Analysis")
                    .addData("error", "No data available")
                    .build();
        }
        
        // Build analysis result
        AnalysisResultBuilder builder = new AnalysisResultBuilder()
                .analysisType("Statewise Analysis");
        
        // Find state with highest confirmed cases
        String stateWithHighestCases = "";
        long highestCases = 0;
        
        // Find state with highest deaths
        String stateWithHighestDeaths = "";
        long highestDeaths = 0;
        
        // Find state with highest recovery
        String stateWithHighestRecovery = "";
        long highestRecovery = 0;
        
        // Calculate totals
        long totalConfirmed = 0;
        long totalDeaths = 0;
        long totalCured = 0;
        
        for (Object[] data : statewiseData) {
            String state = (String) data[0];
            long confirmed = ((Number) data[1]).longValue();
            long deaths = ((Number) data[2]).longValue();
            long cured = ((Number) data[3]).longValue();
            
            totalConfirmed += confirmed;
            totalDeaths += deaths;
            totalCured += cured;
            
            if (confirmed > highestCases) {
                highestCases = confirmed;
                stateWithHighestCases = state;
            }
            
            if (deaths > highestDeaths) {
                highestDeaths = deaths;
                stateWithHighestDeaths = state;
            }
            
            if (cured > highestRecovery) {
                highestRecovery = cured;
                stateWithHighestRecovery = state;
            }
            
            // Add individual state data
            builder.addData(state + "_confirmed", confirmed);
            builder.addData(state + "_deaths", deaths);
            builder.addData(state + "_cured", cured);
        }
        
        // Add summary statistics
        builder.addData("totalConfirmed", totalConfirmed);
        builder.addData("totalDeaths", totalDeaths);
        builder.addData("totalCured", totalCured);
        builder.addData("stateWithHighestCases", stateWithHighestCases);
        builder.addData("highestCases", highestCases);
        builder.addData("stateWithHighestDeaths", stateWithHighestDeaths);
        builder.addData("highestDeaths", highestDeaths);
        builder.addData("stateWithHighestRecovery", stateWithHighestRecovery);
        builder.addData("highestRecovery", highestRecovery);
        
        // Add rates if requested
        if ((Boolean) mergedParams.getOrDefault("includeMortalityRate", true)) {
            builder.addData("mortalityRate", (double) totalDeaths / totalConfirmed * 100);
        }
        
        if ((Boolean) mergedParams.getOrDefault("includeRecoveryRate", true)) {
            builder.addData("recoveryRate", (double) totalCured / totalConfirmed * 100);
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
        List<Object[]> statewiseData = (List<Object[]>) mergedParams.get("statewiseData");
        
        if (statewiseData == null || statewiseData.isEmpty()) {
            return new ChartDataBuilder()
                    .chartType("bar")
                    .title("No Data Available")
                    .build();
        }
        
        // Limit to top N states
        int topStatesCount = (Integer) mergedParams.getOrDefault("topStatesCount", 10);
        int limit = Math.min(statewiseData.size(), topStatesCount);
        
        // Prepare chart data
        List<String> labels = new ArrayList<>();
        List<Number> confirmedCases = new ArrayList<>();
        List<Number> deaths = new ArrayList<>();
        List<Number> cured = new ArrayList<>();
        
        for (int i = 0; i < limit; i++) {
            Object[] data = statewiseData.get(i);
            labels.add((String) data[0]);
            confirmedCases.add((Number) data[1]);
            deaths.add((Number) data[2]);
            cured.add((Number) data[3]);
        }
        
        // Build chart data
        ChartDataBuilder builder = new ChartDataBuilder()
                .chartType("bar")
                .title("COVID-19 Cases by State (Top " + limit + ")")
                .xAxisLabel("States")
                .yAxisLabel("Number of Cases")
                .labels(labels)
                .dataset("Confirmed Cases", confirmedCases)
                .dataset("Deaths", deaths)
                .dataset("Cured", cured);
        
        return builder.build();
    }
}
