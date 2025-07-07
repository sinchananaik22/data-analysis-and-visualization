package com.project.covid.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
public class CovidData {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String sno;
    private LocalDate date;
    private String time;
    private String state;
    private String confirmedIndianNational;
    private String confirmedForeignNational;
    private int cured;
    private int deaths;
    private int confirmed;
    
    // Default constructor required by JPA
    public CovidData() {
    }
    
    // Constructor for CSV parsing
    public CovidData(String sno, String dateStr, String time, String state, 
                    String confirmedIndianNational, String confirmedForeignNational, 
                    String cured, String deaths, String confirmed) {
        this.sno = sno;
        
        // Parse date from string (DD/MM/YY format)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        try {
            this.date = LocalDate.parse(dateStr, formatter);
        } catch (Exception e) {
            // Handle invalid date format
            this.date = LocalDate.now();
        }
        
        this.time = time;
        this.state = state;
        this.confirmedIndianNational = confirmedIndianNational;
        this.confirmedForeignNational = confirmedForeignNational;
        
        // Parse numeric values, defaulting to 0 if parsing fails
        this.cured = parseIntSafely(cured);
        this.deaths = parseIntSafely(deaths);
        this.confirmed = parseIntSafely(confirmed);
    }
    
    private int parseIntSafely(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getConfirmedIndianNational() {
        return confirmedIndianNational;
    }

    public void setConfirmedIndianNational(String confirmedIndianNational) {
        this.confirmedIndianNational = confirmedIndianNational;
    }

    public String getConfirmedForeignNational() {
        return confirmedForeignNational;
    }

    public void setConfirmedForeignNational(String confirmedForeignNational) {
        this.confirmedForeignNational = confirmedForeignNational;
    }

    public int getCured() {
        return cured;
    }

    public void setCured(int cured) {
        this.cured = cured;
    }

    public int getDeaths() {
        return deaths;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }

    public int getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(int confirmed) {
        this.confirmed = confirmed;
    }
    
    @Override
    public String toString() {
        return "CovidData{" +
                "id=" + id +
                ", date=" + date +
                ", state='" + state + '\'' +
                ", confirmed=" + confirmed +
                ", cured=" + cured +
                ", deaths=" + deaths +
                '}';
    }
}
