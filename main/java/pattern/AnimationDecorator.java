package com.project.covid.pattern.decorator;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.WritableValue;
import javafx.scene.chart.Chart;
import javafx.scene.chart.XYChart;
import javafx.util.Duration;
import org.springframework.stereotype.Component;

@Component
public class AnimationDecorator implements ChartDecorator {

    @Override
    public Chart decorate(Chart chart) {
        if (chart instanceof XYChart) {
            XYChart<?, ?> xyChart = (XYChart<?, ?>) chart;

            // Iterate over the series in the XYChart
            for (XYChart.Series<?, ?> series : xyChart.getData()) {
                // Iterate over the data points in each series
                for (XYChart.Data<?, ?> data : series.getData()) {
                    Object yValue = data.getYValue();

                    // Ensure the Y value is a number
                    if (yValue instanceof Number) {
                        Number number = (Number) yValue;
                        Double endValue = number.doubleValue();

                        // Access YValueProperty as a WritableValue (of type Double)
                        WritableValue<Double> yProperty = (WritableValue<Double>) data.YValueProperty();

                        // Create a Timeline animation for the YValueProperty
                        Timeline timeline = new Timeline(
                            new KeyFrame(Duration.ZERO,
                                new KeyValue(yProperty, 0.0)
                            ),
                            new KeyFrame(Duration.millis(1000),
                                new KeyValue(yProperty, endValue)
                            )
                        );
                        timeline.setCycleCount(1);
                        timeline.play();
                    }
                }
            }
        }

        return chart;
    }
}
