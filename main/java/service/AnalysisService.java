package com.project.covid.service;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.pattern.prototype.AnalysisTemplate;
import com.project.covid.pattern.singleton.AnalysisTemplateRegistry;
import com.project.covid.repository.CovidDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for generating analysis results using templates
 */
@Service
public class AnalysisService {
    
    @Autowired
    private CovidDataRepository covidDataRepository;
    
    private final AnalysisTemplateRegistry templateRegistry;
    
    public AnalysisService() {
        this.templateRegistry = AnalysisTemplateRegistry.getInstance();
    }
    
    public AnalysisResult generateAnalysis(String templateKey) {
        AnalysisTemplate template = templateRegistry.getTemplate(templateKey);
        
        Map<String, Object> parameters = new HashMap<>();
        
        if ("statewise".equals(templateKey)) {
            parameters.put("statewiseData", covidDataRepository.getStatewiseAggregatedData());
        } else if ("timeseries".equals(templateKey)) {
            parameters.put("datewiseData", covidDataRepository.getDatewiseAggregatedData());
        }
        
        return template.generateAnalysis(parameters);
    }
    
    public ChartData generateChartData(String templateKey) {
        AnalysisTemplate template = templateRegistry.getTemplate(templateKey);
        
        Map<String, Object> parameters = new HashMap<>();
        
        if ("statewise".equals(templateKey)) {
            parameters.put("statewiseData", covidDataRepository.getStatewiseAggregatedData());
        } else if ("timeseries".equals(templateKey)) {
            parameters.put("datewiseData", covidDataRepository.getDatewiseAggregatedData());
        }
        
        return template.generateChartData(parameters);
    }
    
    public Map<String, String> getAvailableTemplates() {
        return templateRegistry.getAvailableTemplates();
    }
}
