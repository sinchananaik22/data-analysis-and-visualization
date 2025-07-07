package com.project.covid.service;

import com.project.covid.model.AnalysisResult;
import com.project.covid.model.CovidData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

@Service
public class ExportService {
    
    // Export analysis results to CSV
    public byte[] exportAnalysisResultToCSV(AnalysisResult result) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (Writer writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("Analysis Type", "Generated Date", "Key", "Value"))) {
            
            Map<String, Object> data = result.getResultData();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                csvPrinter.printRecord(
                        result.getAnalysisType(),
                        result.getGeneratedDate(),
                        entry.getKey(),
                        entry.getValue()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
        }
    }
    
    // Export raw COVID data to CSV
    public byte[] exportCovidDataToCSV(List<CovidData> covidDataList) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try (Writer writer = new OutputStreamWriter(out);
             CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT
                     .withHeader("Date", "State", "Confirmed", "Cured", "Deaths"))) {
            
            for (CovidData data : covidDataList) {
                csvPrinter.printRecord(
                        data.getDate(),
                        data.getState(),
                        data.getConfirmed(),
                        data.getCured(),
                        data.getDeaths()
                );
            }
            
            csvPrinter.flush();
            return out.toByteArray();
        }
    }
    
    // Export data as JSON
    public String exportAsJson(Object data) {
        // In a real application, you would use a library like Jackson or Gson
        // For simplicity, we'll just return a placeholder
        return "{ \"data\": \"JSON representation of the data would be here\" }";
    }
}
