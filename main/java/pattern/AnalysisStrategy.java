package com.project.covid.pattern.strategy;

import com.project.covid.model.AnalysisResult;
import com.project.covid.repository.CovidDataRepository;

// Strategy Pattern: Interface for different analysis strategies
public interface AnalysisStrategy {
    AnalysisResult analyze(CovidDataRepository repository);
}
