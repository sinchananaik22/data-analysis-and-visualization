package com.project.covid.controller;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.model.CustomAnalysis;
import com.project.covid.pattern.strategy.AnalysisStrategy;
import com.project.covid.pattern.strategy.GrowthRateAnalysisStrategy;
import com.project.covid.pattern.strategy.StatewiseAnalysisStrategy;
import com.project.covid.pattern.strategy.TimeSeriesAnalysisStrategy;
import com.project.covid.service.CovidDataService;
import com.project.covid.service.CustomAnalysisService;
import com.project.covid.service.ExportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/analysis")
public class AnalysisController {
    
    @Autowired
    private CovidDataService covidDataService;
    
    @Autowired
    private ExportService exportService;
    
    @Autowired
    private CustomAnalysisService customAnalysisService;
    
    @Autowired
    private StatewiseAnalysisStrategy statewiseAnalysisStrategy;
    
    @Autowired
    private TimeSeriesAnalysisStrategy timeSeriesAnalysisStrategy;
    
    @Autowired
    private GrowthRateAnalysisStrategy growthRateAnalysisStrategy;
    
    @GetMapping
    public String showAnalysisPage(Model model) {
        model.addAttribute("states", covidDataService.getAllStates());
        model.addAttribute("savedAnalyses", customAnalysisService.getAllCustomAnalyses());
        return "analysis";
    }
    
    @GetMapping("/{type}")
    public String showSpecificAnalysis(@PathVariable String type, Model model) {
        AnalysisStrategy strategy;
        
        switch (type) {
            case "statewise":
                strategy = statewiseAnalysisStrategy;
                break;
            case "timeseries":
                strategy = timeSeriesAnalysisStrategy;
                break;
            case "growthrate":
                strategy = growthRateAnalysisStrategy;
                break;
            default:
                return "redirect:/analysis";
        }
        
        AnalysisResult result = covidDataService.analyzeData(strategy);
        model.addAttribute("result", result);
        model.addAttribute("analysisType", type);
        
        return "analysis-result";
    }
    
    @GetMapping("/export/{type}")
    public ResponseEntity<byte[]> exportAnalysis(@PathVariable String type, 
                                                @RequestParam(defaultValue = "csv") String format) throws IOException {
        AnalysisStrategy strategy;
        
        switch (type) {
            case "statewise":
                strategy = statewiseAnalysisStrategy;
                break;
            case "timeseries":
                strategy = timeSeriesAnalysisStrategy;
                break;
            case "growthrate":
                strategy = growthRateAnalysisStrategy;
                break;
            default:
                return ResponseEntity.badRequest().build();
        }
        
        AnalysisResult result = covidDataService.analyzeData(strategy);
        
        HttpHeaders headers = new HttpHeaders();
        
        if ("csv".equalsIgnoreCase(format)) {
            byte[] csvData = exportService.exportAnalysisResultToCSV(result);
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", type + "-analysis.csv");
            return ResponseEntity.ok().headers(headers).body(csvData);
        } else if ("json".equalsIgnoreCase(format)) {
            // In a real application, you would use a proper JSON serialization
            String jsonData = exportService.exportAsJson(result);
            headers.setContentType(MediaType.APPLICATION_JSON);
            return ResponseEntity.ok().headers(headers).body(jsonData.getBytes());
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/custom")
    public String generateCustomAnalysis(
            @RequestParam(required = false) String state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false, defaultValue = "confirmed") String[] metrics,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String description,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("Generating custom analysis with parameters:");
        System.out.println("State: " + state);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Metrics: " + Arrays.toString(metrics));
        System.out.println("Title: " + title);
        
        List<String> metricsList = metrics != null ? Arrays.asList(metrics) : Arrays.asList("confirmed");
        
        try {
            // Save the custom analysis to the database
            CustomAnalysis savedAnalysis = customAnalysisService.saveCustomAnalysis(
                    state, startDate, endDate, metricsList, title, description);
            
            System.out.println("Analysis saved with ID: " + savedAnalysis.getId());
            
            // Add success message
            redirectAttributes.addFlashAttribute("message", "Custom analysis generated and saved successfully!");
            
            // Redirect to the custom analysis result page
            return "redirect:/analysis/custom/" + savedAnalysis.getId();
        } catch (Exception e) {
            System.err.println("Error saving custom analysis: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message
            redirectAttributes.addFlashAttribute("error", "Error saving analysis: " + e.getMessage());
            
            // Redirect back to analysis page
            return "redirect:/analysis";
        }
    }
    
    @GetMapping("/custom/{id}")
    public String showCustomAnalysisResult(@PathVariable Long id, Model model) {
        CustomAnalysis customAnalysis = customAnalysisService.getCustomAnalysisById(id);
        
        if (customAnalysis == null) {
            return "redirect:/analysis";
        }
        
        AnalysisResult analysisResult = customAnalysisService.getAnalysisResultFromCustomAnalysis(customAnalysis);
        ChartData chartData = customAnalysisService.getChartDataFromCustomAnalysis(customAnalysis);
        
        model.addAttribute("customAnalysis", customAnalysis);
        model.addAttribute("result", analysisResult);
        model.addAttribute("chartData", chartData);
        model.addAttribute("analysisType", "custom");
        
        return "custom-analysis-result";
    }
    
    @GetMapping("/saved")
    public String showSavedAnalyses(Model model) {
        List<CustomAnalysis> savedAnalyses = customAnalysisService.getAllCustomAnalyses();
        model.addAttribute("savedAnalyses", savedAnalyses);
        return "saved-analyses";
    }
}
