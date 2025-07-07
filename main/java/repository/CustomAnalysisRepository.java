package com.project.covid.repository;

import com.project.covid.model.CustomAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomAnalysisRepository extends JpaRepository<CustomAnalysis, Long> {
    
    List<CustomAnalysis> findByStateOrderByCreatedAtDesc(String state);
    
    List<CustomAnalysis> findAllByOrderByCreatedAtDesc();
}
