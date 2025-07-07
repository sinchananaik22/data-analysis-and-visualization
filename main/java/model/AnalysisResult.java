package com.project.covid.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class AnalysisResult {
    
    private String analysisType;
    private Map<String, Object> resultData;
    private LocalDate generatedDate;
    
    public AnalysisResult() {
        this.generatedDate = LocalDate.now();
    }
    
    public AnalysisResult(String analysisType, Map<String, Object> resultData) {
        this.analysisType = analysisType;
        this.resultData = resultData;
        this.generatedDate = LocalDate.now();
    }
    
    // Getters and setters
    public String getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
    
    public Map<String, Object> getResultData() {
        return resultData;
    }
    
    public void setResultData(Map<String, Object> resultData) {
        this.resultData = resultData;
    }
    
    public LocalDate getGeneratedDate() {
        return generatedDate;
    }
    
    public void setGeneratedDate(LocalDate generatedDate) {
        this.generatedDate = generatedDate;
    }
    
    @Override
    public String toString() {
        return "AnalysisResult{" +
                "analysisType='" + analysisType + '\'' +
                ", resultData=" + resultData +
                ", generatedDate=" + generatedDate +
                '}';
    }
}
