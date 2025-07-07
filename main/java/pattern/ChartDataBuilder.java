package com.project.covid.pattern.builder;

import com.project.covid.model.ChartData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder Pattern: Creates ChartData objects with a fluent interface
 */
public class ChartDataBuilder {
    private String chartType;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private List<String> labels = new ArrayList<>();
    private Map<String, List<Number>> datasets = new HashMap<>();
    
    public ChartDataBuilder() {
        // Default constructor
    }
    
    public ChartDataBuilder chartType(String chartType) {
        this.chartType = chartType;
        return this;
    }
    
    public ChartDataBuilder title(String title) {
        this.title = title;
        return this;
    }
    
    public ChartDataBuilder xAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
        return this;
    }
    
    public ChartDataBuilder yAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
        return this;
    }
    
    public ChartDataBuilder labels(List<String> labels) {
        this.labels = new ArrayList<>(labels);
        return this;
    }
    
    public ChartDataBuilder addLabel(String label) {
        this.labels.add(label);
        return this;
    }
    
    public ChartDataBuilder dataset(String name, List<Number> data) {
        this.datasets.put(name, new ArrayList<>(data));
        return this;
    }
    
    public ChartDataBuilder addDataPoint(String datasetName, Number value) {
        if (!this.datasets.containsKey(datasetName)) {
            this.datasets.put(datasetName, new ArrayList<>());
        }
        this.datasets.get(datasetName).add(value);
        return this;
    }
    
    public ChartData build() {
        return new ChartData(chartType, title, xAxisLabel, yAxisLabel, labels, datasets);
    }
}
