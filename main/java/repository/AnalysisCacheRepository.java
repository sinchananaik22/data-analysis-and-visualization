package com.project.covid.repository;

import com.project.covid.model.AnalysisCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnalysisCacheRepository extends JpaRepository<AnalysisCache, Long> {
    
    Optional<AnalysisCache> findByAnalysisTypeAndAnalysisKey(String analysisType, String analysisKey);
    
    void deleteByAnalysisTypeAndAnalysisKey(String analysisType, String analysisKey);
}
