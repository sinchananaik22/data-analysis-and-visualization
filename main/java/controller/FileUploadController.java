package com.project.covid.controller;

import com.project.covid.model.CovidData;
import com.project.covid.service.CovidDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/upload")
public class FileUploadController {
    
    @Autowired
    private CovidDataService covidDataService;
    
    @GetMapping
    public String showUploadForm() {
        return "upload";
    }
    
    @PostMapping
    public String handleFileUpload(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return "redirect:/upload";
        }
        
        try {
            List<CovidData> processedData = covidDataService.processAndSaveCSV(file);
            redirectAttributes.addFlashAttribute("message", 
                    "Successfully uploaded and processed " + processedData.size() + " records");
            return "redirect:/dashboard";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("message", 
                    "Failed to upload and process file: " + e.getMessage());
            return "redirect:/upload";
        }
    }
    
    @PostMapping("/api")
    public ResponseEntity<?> handleFileUploadApi(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }
        
        try {
            List<CovidData> processedData = covidDataService.processAndSaveCSV(file);
            return ResponseEntity.ok().body("Successfully uploaded and processed " + processedData.size() + " records");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to upload and process file: " + e.getMessage());
        }
    }
}
