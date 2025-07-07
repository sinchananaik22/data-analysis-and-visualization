package com.project.covid.pattern.factory;

import com.project.covid.model.ChartData;
import com.project.covid.pattern.singleton.ChartConfigurationManager;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.util.List;
import java.util.Map;

/**
 * Factory Pattern: Creates different types of charts
 */
public class ChartFactory {
    
    private final ChartConfigurationManager configManager;
    
    public ChartFactory() {
        this.configManager = ChartConfigurationManager.getInstance();
    }
    
    public Chart createChart(ChartData chartData) {
        String chartType = chartData.getChartType().toLowerCase();
        Map<String, Object> config = configManager.getChartConfiguration(chartType);
        
        switch (chartType) {
            case "line":
                return createLineChart(chartData, config);
            case "bar":
                return createBarChart(chartData, config);
            case "pie":
                return createPieChart(chartData, config);
            default:
                throw new IllegalArgumentException("Unsupported chart type: " + chartType);
        }
    }
    
    private LineChart<String, Number> createLineChart(ChartData chartData, Map<String, Object> config) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        xAxis.setLabel(chartData.getxAxisLabel());
        yAxis.setLabel(chartData.getyAxisLabel());
        
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(chartData.getTitle());
        
        // Apply configuration
        lineChart.setLegendVisible((Boolean) config.getOrDefault("showLegend", true));
        lineChart.setAnimated((Boolean) config.getOrDefault("animation", true));
        
        // Add data series
        Map<String, List<Number>> datasets = chartData.getDatasets();
        List<String> labels = chartData.getLabels();
        
        for (Map.Entry<String, List<Number>> entry : datasets.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());
            
            List<Number> data = entry.getValue();
            for (int i = 0; i < data.size() && i < labels.size(); i++) {
                series.getData().add(new XYChart.Data<>(labels.get(i), data.get(i)));
            }
            
            lineChart.getData().add(series);
        }
        
        return lineChart;
    }
    
    private BarChart<String, Number> createBarChart(ChartData chartData, Map<String, Object> config) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        
        xAxis.setLabel(chartData.getxAxisLabel());
        yAxis.setLabel(chartData.getyAxisLabel());
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle(chartData.getTitle());
        
        // Apply configuration
        barChart.setLegendVisible((Boolean) config.getOrDefault("showLegend", true));
        barChart.setAnimated((Boolean) config.getOrDefault("animation", true));
        
        // Add data series
        Map<String, List<Number>> datasets = chartData.getDatasets();
        List<String> labels = chartData.getLabels();
        
        for (Map.Entry<String, List<Number>> entry : datasets.entrySet()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(entry.getKey());
            
            List<Number> data = entry.getValue();
            for (int i = 0; i < data.size() && i < labels.size(); i++) {
                series.getData().add(new XYChart.Data<>(labels.get(i), data.get(i)));
            }
            
            barChart.getData().add(series);
        }
        
        return barChart;
    }
    
    private PieChart createPieChart(ChartData chartData, Map<String, Object> config) {
        // For pie chart, we'll use the first dataset only
        Map<String, List<Number>> datasets = chartData.getDatasets();
        List<String> labels = chartData.getLabels();
        
        if (datasets.isEmpty() || labels.isEmpty()) {
            throw new IllegalArgumentException("Pie chart requires data");
        }
        
        // Get the first dataset
        List<Number> data = datasets.values().iterator().next();
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (int i = 0; i < data.size() && i < labels.size(); i++) {
            pieChartData.add(new PieChart.Data(labels.get(i), data.get(i).doubleValue()));
        }
        
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle(chartData.getTitle());
        
        // Apply configuration
        pieChart.setLegendVisible((Boolean) config.getOrDefault("showLegend", true));
        pieChart.setAnimated((Boolean) config.getOrDefault("animation", true));
        
        return pieChart;
    }
}
