package com.project.covid.model;

import java.util.List;
import java.util.Map;

public class ChartData {
    
    private String chartType;
    private String title;
    private String xAxisLabel;
    private String yAxisLabel;
    private List<String> labels;
    private Map<String, List<Number>> datasets;
    
    public ChartData() {
    }
    
    public ChartData(String chartType, String title, String xAxisLabel, String yAxisLabel, 
                    List<String> labels, Map<String, List<Number>> datasets) {
        this.chartType = chartType;
        this.title = title;
        this.xAxisLabel = xAxisLabel;
        this.yAxisLabel = yAxisLabel;
        this.labels = labels;
        this.datasets = datasets;
    }
    
    // Getters and setters
    public String getChartType() {
        return chartType;
    }
    
    public void setChartType(String chartType) {
        this.chartType = chartType;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getxAxisLabel() {
        return xAxisLabel;
    }
    
    public void setxAxisLabel(String xAxisLabel) {
        this.xAxisLabel = xAxisLabel;
    }
    
    public String getyAxisLabel() {
        return yAxisLabel;
    }
    
    public void setyAxisLabel(String yAxisLabel) {
        this.yAxisLabel = yAxisLabel;
    }
    
    public List<String> getLabels() {
        return labels;
    }
    
    public void setLabels(List<String> labels) {
        this.labels = labels;
    }
    
    public Map<String, List<Number>> getDatasets() {
        return datasets;
    }
    
    public void setDatasets(Map<String, List<Number>> datasets) {
        this.datasets = datasets;
    }
}
