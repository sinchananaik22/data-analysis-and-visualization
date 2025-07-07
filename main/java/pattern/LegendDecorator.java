package com.project.covid.pattern.decorator;

import javafx.scene.chart.Chart;
import org.springframework.stereotype.Component;

@Component
public class LegendDecorator implements ChartDecorator {
    
    private final boolean showLegend;
    
    public LegendDecorator() {
        this.showLegend = true;
    }
    
    public LegendDecorator(boolean showLegend) {
        this.showLegend = showLegend;
    }
    
    @Override
    public Chart decorate(Chart chart) {
        chart.setLegendVisible(showLegend);
        return chart;
    }
}
