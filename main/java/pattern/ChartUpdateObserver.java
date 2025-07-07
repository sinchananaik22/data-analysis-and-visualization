package com.project.covid.pattern.observer;

import com.project.covid.service.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChartUpdateObserver implements DataChangeObserver {
    
    @Autowired
    private CovidDataService covidDataService;
    
    @Override
    public void onDataChange() {
        // This would typically update charts or visualizations
        System.out.println("Data has changed. Charts will be updated.");
        
        // In a real application, this might trigger WebSocket events to update UI
        // or refresh cached chart data
    }
}
