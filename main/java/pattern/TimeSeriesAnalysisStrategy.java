package com.project.covid.pattern.strategy;

import com.project.covid.model.AnalysisResult;
import com.project.covid.repository.CovidDataRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TimeSeriesAnalysisStrategy implements AnalysisStrategy {
    
    @Override
    public AnalysisResult analyze(CovidDataRepository repository) {
        List<Object[]> datewiseData = repository.getDatewiseAggregatedData();
        
        Map<String, Object> resultData = new HashMap<>();
        
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
            
            // Calculate daily changes
            long newCases = confirmed - previousDayConfirmed;
            long newDeaths = deaths - previousDayDeaths;
            long newRecoveries = cured - previousDayCured;
            
            if (i > 0) {  // Skip first day as we can't calculate change
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
                resultData.put(date + "_newCases", newCases);
                resultData.put(date + "_newDeaths", newDeaths);
                resultData.put(date + "_newRecoveries", newRecoveries);
            }
            
            // Add cumulative data
            resultData.put(date + "_confirmed", confirmed);
            resultData.put(date + "_deaths", deaths);
            resultData.put(date + "_cured", cured);
            
            // Update previous day values
            previousDayConfirmed = confirmed;
            previousDayDeaths = deaths;
            previousDayCured = cured;
        }
        
        // Add summary statistics
        if (dateWithHighestNewCases != null) {
            resultData.put("dateWithHighestNewCases", dateWithHighestNewCases);
            resultData.put("highestNewCases", highestNewCases);
        }
        
        if (dateWithHighestNewDeaths != null) {
            resultData.put("dateWithHighestNewDeaths", dateWithHighestNewDeaths);
            resultData.put("highestNewDeaths", highestNewDeaths);
        }
        
        if (dateWithHighestNewRecoveries != null) {
            resultData.put("dateWithHighestNewRecoveries", dateWithHighestNewRecoveries);
            resultData.put("highestNewRecoveries", highestNewRecoveries);
        }
        
        // Calculate growth rates if we have enough data
        if (datewiseData.size() > 1) {
            Object[] firstDay = datewiseData.get(0);
            Object[] lastDay = datewiseData.get(datewiseData.size() - 1);
            
            long firstDayConfirmed = ((Number) firstDay[1]).longValue();
            long lastDayConfirmed = ((Number) lastDay[1]).longValue();
            
            if (firstDayConfirmed > 0) {
                double growthRate = (double) (lastDayConfirmed - firstDayConfirmed) / firstDayConfirmed * 100;
                resultData.put("overallGrowthRate", growthRate);
            }
        }
        
        return new AnalysisResult("Time Series Analysis", resultData);
    }
}
