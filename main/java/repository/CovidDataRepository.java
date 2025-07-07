package com.project.covid.repository;

import com.project.covid.model.CovidData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface CovidDataRepository extends JpaRepository<CovidData, Long> {
    
    List<CovidData> findByState(String state);
    
    List<CovidData> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<CovidData> findByStateAndDateBetween(String state, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT c.state, SUM(c.confirmed) as totalConfirmed, SUM(c.deaths) as totalDeaths, SUM(c.cured) as totalCured " +
           "FROM CovidData c GROUP BY c.state ORDER BY totalConfirmed DESC")
    List<Object[]> getStatewiseAggregatedData();
    
    @Query("SELECT c.date, SUM(c.confirmed) as totalConfirmed, SUM(c.deaths) as totalDeaths, SUM(c.cured) as totalCured " +
           "FROM CovidData c GROUP BY c.date ORDER BY c.date")
    List<Object[]> getDatewiseAggregatedData();
    
    @Query("SELECT c FROM CovidData c WHERE c.state = ?1 ORDER BY c.date")
    List<CovidData> getStateDataChronologically(String state);
    
    @Query("SELECT DISTINCT c.state FROM CovidData c ORDER BY c.state")
    List<String> getAllStates();
}
