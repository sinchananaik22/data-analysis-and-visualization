package com.project.covid.pattern.prototype;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype Pattern: Base class for analysis templates that can be cloned
 */
public abstract class AnalysisTemplate implements Cloneable {
    protected String templateName;
    protected String description;
    protected Map<String, Object> defaultParameters;
    
    public AnalysisTemplate() {
        this.defaultParameters = new HashMap<>();
    }
    
    public AnalysisTemplate(String templateName, String description) {
        this.templateName = templateName;
        this.description = description;
        this.defaultParameters = new HashMap<>();
    }
    
    public abstract AnalysisResult generateAnalysis(Map<String, Object> parameters);
    
    public abstract ChartData generateChartData(Map<String, Object> parameters);
    
    public String getTemplateName() {
        return templateName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Map<String, Object> getDefaultParameters() {
        return new HashMap<>(defaultParameters);
    }
    
    @Override
    public AnalysisTemplate clone() {
        try {
            AnalysisTemplate clone = (AnalysisTemplate) super.clone();
            clone.defaultParameters = new HashMap<>(this.defaultParameters);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone analysis template", e);
        }
    }
}
