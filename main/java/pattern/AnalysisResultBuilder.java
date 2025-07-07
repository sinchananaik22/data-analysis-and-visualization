package com.project.covid.pattern.builder;

import com.project.covid.model.AnalysisResult;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Builder Pattern: Creates AnalysisResult objects with a fluent interface
 */
public class AnalysisResultBuilder {
    private String analysisType;
    private Map<String, Object> resultData = new HashMap<>();
    private LocalDate generatedDate = LocalDate.now();
    
    public AnalysisResultBuilder() {
        // Default constructor
    }
    
    public AnalysisResultBuilder analysisType(String analysisType) {
        this.analysisType = analysisType;
        return this;
    }
    
    public AnalysisResultBuilder addData(String key, Object value) {
        this.resultData.put(key, value);
        return this;
    }
    
    public AnalysisResultBuilder addAllData(Map<String, Object> data) {
        this.resultData.putAll(data);
        return this;
    }
    
    public AnalysisResultBuilder generatedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
        return this;
    }
    
    public AnalysisResult build() {
        AnalysisResult result = new AnalysisResult(analysisType, resultData);
        result.setGeneratedDate(generatedDate);
        return result;
    }
}
