package com.project.covid.pattern.strategy;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.CovidData;
import com.project.covid.repository.CovidDataRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class GrowthRateAnalysisStrategy implements AnalysisStrategy {
    
    @Override
    public AnalysisResult analyze(CovidDataRepository repository) {
        List<Object[]> datewiseData = repository.getDatewiseAggregatedData();
        
        Map<String, Object> resultData = new HashMap<>();
        
        // Calculate growth rates for each consecutive day
        List<Double> dailyGrowthRates = new ArrayList<>();
        List<Double> weeklyGrowthRates = new ArrayList<>();
        
        long previousDayConfirmed = 0;
        long sevenDaysAgoConfirmed = 0;
        
        for (int i = 0; i < datewiseData.size(); i++) {
            Object[] data = datewiseData.get(i);
            long confirmed = ((Number) data[1]).longValue();
            
            // Calculate daily growth rate
            if (i > 0 && previousDayConfirmed > 0) {
                double dailyGrowthRate = (double) (confirmed - previousDayConfirmed) / previousDayConfirmed * 100;
                dailyGrowthRates.add(dailyGrowthRate);
                resultData.put("day_" + i + "_growthRate", dailyGrowthRate);
            }
            
            // Calculate weekly growth rate
            if (i >= 7 && sevenDaysAgoConfirmed > 0) {
                double weeklyGrowthRate = (double) (confirmed - sevenDaysAgoConfirmed) / sevenDaysAgoConfirmed * 100;
                weeklyGrowthRates.add(weeklyGrowthRate);
                resultData.put("week_" + (i/7) + "_growthRate", weeklyGrowthRate);
            }
            
            // Update previous values
            if (i >= 7) {
                Object[] sevenDaysAgoData = datewiseData.get(i - 7);
                sevenDaysAgoConfirmed = ((Number) sevenDaysAgoData[1]).longValue();
            }
            previousDayConfirmed = confirmed;
        }
        
        // Calculate average growth rates
        double avgDailyGrowthRate = 0;
        if (!dailyGrowthRates.isEmpty()) {
            avgDailyGrowthRate = dailyGrowthRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        }
        
        double avgWeeklyGrowthRate = 0;
        if (!weeklyGrowthRates.isEmpty()) {
            avgWeeklyGrowthRate = weeklyGrowthRates.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        }
        
        resultData.put("averageDailyGrowthRate", avgDailyGrowthRate);
        resultData.put("averageWeeklyGrowthRate", avgWeeklyGrowthRate);
        
        // Calculate doubling time based on average daily growth rate
        if (avgDailyGrowthRate > 0) {
            double doublingTime = 70 / avgDailyGrowthRate; // Rule of 70 approximation
            resultData.put("estimatedDoublingTimeDays", doublingTime);
        }
        
        return new AnalysisResult("Growth Rate Analysis", resultData);
    }
}
