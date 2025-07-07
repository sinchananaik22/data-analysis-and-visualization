package com.project.covid.pattern.singleton;

import com.project.covid.pattern.prototype.AnalysisTemplate;
import com.project.covid.pattern.prototype.StatewiseAnalysisTemplate;
import com.project.covid.pattern.prototype.TimeSeriesAnalysisTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern: Registry for analysis templates
 */
public class AnalysisTemplateRegistry {
    private static AnalysisTemplateRegistry instance;
    private final Map<String, AnalysisTemplate> templates;
    
    private AnalysisTemplateRegistry() {
        templates = new HashMap<>();
        
        // Register default templates
        registerTemplate("statewise", new StatewiseAnalysisTemplate());
        registerTemplate("timeseries", new TimeSeriesAnalysisTemplate());
    }
    
    public static synchronized AnalysisTemplateRegistry getInstance() {
        if (instance == null) {
            instance = new AnalysisTemplateRegistry();
        }
        return instance;
    }
    
    public void registerTemplate(String key, AnalysisTemplate template) {
        templates.put(key, template);
    }
    
    public AnalysisTemplate getTemplate(String key) {
        AnalysisTemplate template = templates.get(key);
        if (template == null) {
            throw new IllegalArgumentException("Template not found: " + key);
        }
        return template.clone();
    }
    
    public boolean hasTemplate(String key) {
        return templates.containsKey(key);
    }
    
    public Map<String, String> getAvailableTemplates() {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, AnalysisTemplate> entry : templates.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getTemplateName());
        }
        return result;
    }
}
