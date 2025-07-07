package com.project.covid.controller;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.pattern.strategy.GrowthRateAnalysisStrategy;
import com.project.covid.pattern.strategy.StateSpecificAnalysisStrategy;
import com.project.covid.pattern.strategy.StatewiseAnalysisStrategy;
import com.project.covid.pattern.strategy.TimeSeriesAnalysisStrategy;
import com.project.covid.service.AnalysisCacheService;
import com.project.covid.service.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    
    @Autowired
    private CovidDataService covidDataService;
    
    @Autowired
    private AnalysisCacheService analysisCacheService;
    
    @Autowired
    private StatewiseAnalysisStrategy statewiseAnalysisStrategy;
    
    @Autowired
    private TimeSeriesAnalysisStrategy timeSeriesAnalysisStrategy;
    
    @Autowired
    private GrowthRateAnalysisStrategy growthRateAnalysisStrategy;
    
    @Autowired
    private StateSpecificAnalysisStrategy stateSpecificAnalysisStrategy;
    
    @GetMapping
    public String showDashboard(Model model) {
        // Get analysis results
        AnalysisResult statewiseAnalysis = covidDataService.analyzeData(statewiseAnalysisStrategy);
        AnalysisResult timeSeriesAnalysis = covidDataService.analyzeData(timeSeriesAnalysisStrategy);
        AnalysisResult growthRateAnalysis = covidDataService.analyzeData(growthRateAnalysisStrategy);
        
        // Cache the analysis results
        analysisCacheService.cacheAnalysisResult("statewise", "all", statewiseAnalysis);
        analysisCacheService.cacheAnalysisResult("timeseries", "all", timeSeriesAnalysis);
        analysisCacheService.cacheAnalysisResult("growthrate", "all", growthRateAnalysis);
        
        // Get chart data
        ChartData stateComparisonChart = covidDataService.prepareStateComparisonChartData();
        ChartData timeSeriesChart = covidDataService.prepareTimeSeriesChartData();
        
        // Cache the chart data
        analysisCacheService.cacheChartData("stateComparison", "all", stateComparisonChart);
        analysisCacheService.cacheChartData("timeSeries", "all", timeSeriesChart);
        
        // Get list of states for dropdown
        List<String> states = covidDataService.getAllStates();
        
        // Add data to model
        model.addAttribute("statewiseAnalysis", statewiseAnalysis);
        model.addAttribute("timeSeriesAnalysis", timeSeriesAnalysis);
        model.addAttribute("growthRateAnalysis", growthRateAnalysis);
        model.addAttribute("stateComparisonChart", stateComparisonChart);
        model.addAttribute("timeSeriesChart", timeSeriesChart);
        model.addAttribute("states", states);
        
        return "dashboard";
    }
    
    @GetMapping("/state-chart")
    @ResponseBody
    public ChartData getStateChart(@RequestParam String state) {
        // Check if we have cached chart data for this state
        Optional<ChartData> cachedChart = analysisCacheService.getCachedChartData("stateChart", state);
        
        if (cachedChart.isPresent()) {
            return cachedChart.get();
        }
        
        // If not cached, generate the chart data
        ChartData stateChart = covidDataService.prepareStateTimeSeriesChartData(state);
        
        // Cache the chart data
        analysisCacheService.cacheChartData("stateChart", state, stateChart);
        
        return stateChart;
    }
    
    @GetMapping("/state-analysis")
    @ResponseBody
    public ResponseEntity<AnalysisResult> getStateAnalysis(@RequestParam String state) {
        // Check if we have cached analysis for this state
        Optional<AnalysisResult> cachedAnalysis = analysisCacheService.getCachedAnalysisResult("stateAnalysis", state);
        
        AnalysisResult stateAnalysis;
        if (cachedAnalysis.isPresent()) {
            stateAnalysis = cachedAnalysis.get();
        } else {
            // If not cached, perform the analysis
            stateSpecificAnalysisStrategy.setState(state);
            stateAnalysis = covidDataService.analyzeData(stateSpecificAnalysisStrategy);
            
            // Cache the analysis result
            analysisCacheService.cacheAnalysisResult("stateAnalysis", state, stateAnalysis);
        }
        
        return ResponseEntity.ok(stateAnalysis);
    }
}
