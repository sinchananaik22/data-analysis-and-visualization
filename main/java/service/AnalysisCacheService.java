package com.project.covid.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.covid.model.AnalysisCache;
import com.project.covid.model.AnalysisResult;
import com.project.covid.model.ChartData;
import com.project.covid.repository.AnalysisCacheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AnalysisCacheService {
    
    @Autowired
    private AnalysisCacheRepository analysisCacheRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void cacheAnalysisResult(String analysisType, String key, AnalysisResult result) {
        try {
            String jsonData = objectMapper.writeValueAsString(result);
            
            Optional<AnalysisCache> existingCache = analysisCacheRepository.findByAnalysisTypeAndAnalysisKey(analysisType, key);
            
            if (existingCache.isPresent()) {
                AnalysisCache cache = existingCache.get();
                cache.setAnalysisData(jsonData);
                cache.setUpdatedAt(LocalDateTime.now());
                analysisCacheRepository.save(cache);
            } else {
                AnalysisCache cache = new AnalysisCache(analysisType, key, jsonData);
                analysisCacheRepository.save(cache);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to cache analysis result", e);
        }
    }
    
    public void cacheChartData(String analysisType, String key, ChartData chartData) {
        try {
            String jsonData = objectMapper.writeValueAsString(chartData);
            
            Optional<AnalysisCache> existingCache = analysisCacheRepository.findByAnalysisTypeAndAnalysisKey(analysisType, key);
            
            if (existingCache.isPresent()) {
                AnalysisCache cache = existingCache.get();
                cache.setAnalysisData(jsonData);
                cache.setUpdatedAt(LocalDateTime.now());
                analysisCacheRepository.save(cache);
            } else {
                AnalysisCache cache = new AnalysisCache(analysisType, key, jsonData);
                analysisCacheRepository.save(cache);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to cache chart data", e);
        }
    }
    
    public Optional<AnalysisResult> getCachedAnalysisResult(String analysisType, String key) {
        Optional<AnalysisCache> cache = analysisCacheRepository.findByAnalysisTypeAndAnalysisKey(analysisType, key);
        
        if (cache.isPresent()) {
            try {
                return Optional.of(objectMapper.readValue(cache.get().getAnalysisData(), AnalysisResult.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse cached analysis result", e);
            }
        }
        
        return Optional.empty();
    }
    
    public Optional<ChartData> getCachedChartData(String analysisType, String key) {
        Optional<AnalysisCache> cache = analysisCacheRepository.findByAnalysisTypeAndAnalysisKey(analysisType, key);
        
        if (cache.isPresent()) {
            try {
                return Optional.of(objectMapper.readValue(cache.get().getAnalysisData(), ChartData.class));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse cached chart data", e);
            }
        }
        
        return Optional.empty();
    }
    
    public void invalidateCache(String analysisType, String key) {
        analysisCacheRepository.deleteByAnalysisTypeAndAnalysisKey(analysisType, key);
    }
}
