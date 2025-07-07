package com.project.covid.pattern.strategy;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.CovidData;
import com.project.covid.repository.CovidDataRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StateSpecificAnalysisStrategy implements AnalysisStrategy {
    
    private String state;
    
    public StateSpecificAnalysisStrategy() {
        // Default constructor
    }
    
    public StateSpecificAnalysisStrategy(String state) {
        this.state = state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    @Override
    public AnalysisResult analyze(CovidDataRepository repository) {
        if (state == null || state.isEmpty()) {
            throw new IllegalArgumentException("State must be specified for state-specific analysis");
        }
        
        List<CovidData> stateData = repository.findByState(state);
        
        if (stateData.isEmpty()) {
            Map<String, Object> emptyResult = new HashMap<>();
            emptyResult.put("message", "No data found for state: " + state);
            return new AnalysisResult("State-specific Analysis: " + state, emptyResult);
        }
        
        Map<String, Object> resultData = new HashMap<>();
        
        // Calculate total cases, deaths, and recoveries
        int totalConfirmed = stateData.stream().mapToInt(CovidData::getConfirmed).sum();
        int totalDeaths = stateData.stream().mapToInt(CovidData::getDeaths).sum();
        int totalCured = stateData.stream().mapToInt(CovidData::getCured).sum();
        
        resultData.put("state", state);
        resultData.put("totalConfirmed", totalConfirmed);
        resultData.put("totalDeaths", totalDeaths);
        resultData.put("totalCured", totalCured);
        
        // Calculate mortality and recovery rates
        double mortalityRate = totalConfirmed > 0 ? (double) totalDeaths / totalConfirmed * 100 : 0;
        double recoveryRate = totalConfirmed > 0 ? (double) totalCured / totalConfirmed * 100 : 0;
        
        resultData.put("mortalityRate", mortalityRate);
        resultData.put("recoveryRate", recoveryRate);
        
        // Find date with highest cases
        CovidData highestCasesDay = stateData.stream()
                .max((a, b) -> Integer.compare(a.getConfirmed(), b.getConfirmed()))
                .orElse(null);
        
        if (highestCasesDay != null) {
            resultData.put("dateWithHighestCases", highestCasesDay.getDate());
            resultData.put("highestCasesCount", highestCasesDay.getConfirmed());
        }
        
        // Calculate daily growth rates
        List<CovidData> sortedData = stateData.stream()
                .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                .collect(Collectors.toList());
        
        if (sortedData.size() > 1) {
            double[] growthRates = new double[sortedData.size() - 1];
            for (int i = 1; i < sortedData.size(); i++) {
                int prevCases = sortedData.get(i-1).getConfirmed();
                int currentCases = sortedData.get(i).getConfirmed();
                
                if (prevCases > 0) {
                    growthRates[i-1] = (double) (currentCases - prevCases) / prevCases * 100;
                } else {
                    growthRates[i-1] = 0;
                }
                
                LocalDate date = sortedData.get(i).getDate();
                resultData.put("growthRate_" + date, growthRates[i-1]);
            }
            
            // Calculate average growth rate
            double avgGrowthRate = 0;
            for (double rate : growthRates) {
                avgGrowthRate += rate;
            }
            avgGrowthRate /= growthRates.length;
            
            resultData.put("averageGrowthRate", avgGrowthRate);
            
            // Estimate doubling time based on average growth rate
            if (avgGrowthRate > 0) {
                double doublingTime = 70 / avgGrowthRate; // Rule of 70 approximation
                resultData.put("estimatedDoublingTimeDays", doublingTime);
            }
        }
        
        // Add first and last dates in the dataset
        if (!sortedData.isEmpty()) {
            resultData.put("firstDate", sortedData.get(0).getDate());
            resultData.put("lastDate", sortedData.get(sortedData.size() - 1).getDate());
        }
        
        return new AnalysisResult("State-specific Analysis: " + state, resultData);
    }
}
