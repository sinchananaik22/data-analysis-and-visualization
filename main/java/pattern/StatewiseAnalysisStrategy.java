package com.project.covid.pattern.strategy;

import com.project.covid.model.AnalysisResult;
import com.project.covid.repository.CovidDataRepository;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StatewiseAnalysisStrategy implements AnalysisStrategy {
    
    @Override
    public AnalysisResult analyze(CovidDataRepository repository) {
        List<Object[]> statewiseData = repository.getStatewiseAggregatedData();
        
        Map<String, Object> resultData = new HashMap<>();
        
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
            resultData.put(state + "_confirmed", confirmed);
            resultData.put(state + "_deaths", deaths);
            resultData.put(state + "_cured", cured);
        }
        
        // Add summary statistics
        resultData.put("totalConfirmed", totalConfirmed);
        resultData.put("totalDeaths", totalDeaths);
        resultData.put("totalCured", totalCured);
        resultData.put("stateWithHighestCases", stateWithHighestCases);
        resultData.put("highestCases", highestCases);
        resultData.put("stateWithHighestDeaths", stateWithHighestDeaths);
        resultData.put("highestDeaths", highestDeaths);
        resultData.put("stateWithHighestRecovery", stateWithHighestRecovery);
        resultData.put("highestRecovery", highestRecovery);
        resultData.put("mortalityRate", (double) totalDeaths / totalConfirmed * 100);
        resultData.put("recoveryRate", (double) totalCured / totalConfirmed * 100);
        
        return new AnalysisResult("Statewise Analysis", resultData);
    }
}
