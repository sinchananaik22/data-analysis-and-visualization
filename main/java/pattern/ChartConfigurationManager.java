package com.project.covid.pattern.singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * Singleton Pattern: Manages chart configurations
 */
public class ChartConfigurationManager {
    private static ChartConfigurationManager instance;
    private final Map<String, Map<String, Object>> chartConfigurations;
    
    private ChartConfigurationManager() {
        chartConfigurations = new HashMap<>();
        
        // Initialize default configurations
        Map<String, Object> lineChartConfig = new HashMap<>();
        lineChartConfig.put("showLegend", true);
        lineChartConfig.put("responsive", true);
        lineChartConfig.put("maintainAspectRatio", false);
        lineChartConfig.put("animation", true);
        
        Map<String, Object> barChartConfig = new HashMap<>();
        barChartConfig.put("showLegend", true);
        barChartConfig.put("responsive", true);
        barChartConfig.put("maintainAspectRatio", false);
        barChartConfig.put("animation", true);
        
        Map<String, Object> pieChartConfig = new HashMap<>();
        pieChartConfig.put("showLegend", true);
        pieChartConfig.put("responsive", true);
        pieChartConfig.put("maintainAspectRatio", false);
        pieChartConfig.put("animation", true);
        
        chartConfigurations.put("line", lineChartConfig);
        chartConfigurations.put("bar", barChartConfig);
        chartConfigurations.put("pie", pieChartConfig);
    }
    
    public static synchronized ChartConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ChartConfigurationManager();
        }
        return instance;
    }
    
    public Map<String, Object> getChartConfiguration(String chartType) {
        Map<String, Object> config = chartConfigurations.get(chartType.toLowerCase());
        if (config == null) {
            return new HashMap<>();
        }
        return new HashMap<>(config);
    }
    
    public void setChartConfiguration(String chartType, Map<String, Object> configuration) {
        chartConfigurations.put(chartType.toLowerCase(), new HashMap<>(configuration));
    }
    
    public void updateChartConfiguration(String chartType, String key, Object value) {
        Map<String, Object> config = chartConfigurations.computeIfAbsent(
                chartType.toLowerCase(), k -> new HashMap<>());
        config.put(key, value);
    }
}
