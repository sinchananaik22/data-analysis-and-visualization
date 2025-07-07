package com.project.covid.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import java.time.LocalDateTime;

@Entity
public class AnalysisCache {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String analysisType;
    
    private String analysisKey;
    
    @Lob
    @Column(length = 100000)
    private String analysisData;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public AnalysisCache() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public AnalysisCache(String analysisType, String analysisKey, String analysisData) {
        this.analysisType = analysisType;
        this.analysisKey = analysisKey;
        this.analysisData = analysisData;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getAnalysisType() {
        return analysisType;
    }
    
    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }
    
    public String getAnalysisKey() {
        return analysisKey;
    }
    
    public void setAnalysisKey(String analysisKey) {
        this.analysisKey = analysisKey;
    }
    
    public String getAnalysisData() {
        return analysisData;
    }
    
    public void setAnalysisData(String analysisData) {
        this.analysisData = analysisData;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
