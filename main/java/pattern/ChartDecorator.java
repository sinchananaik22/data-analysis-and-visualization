package com.project.covid.pattern.decorator;

import javafx.scene.chart.Chart;

// Decorator Pattern: Interface for chart decorators
public interface ChartDecorator {
    Chart decorate(Chart chart);
}
