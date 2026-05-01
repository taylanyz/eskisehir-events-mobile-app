package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.Poi;
import com.eskisehir.eventapi.domain.model.User;
import java.util.List;
import java.util.Map;

/**
 * Represents the complete optimization request with all user preferences,
 * constraints, and context information for route generation.
 */
public class RouteOptimizationRequest {
    private Long userId;
    private Double originLat;
    private Double originLng;
    private List<Long> candidatePoiIds;
    
    // Budget constraints
    private Integer timeBudgetMinutes;
    private Double distanceBudgetKm;
    private Double monetaryBudgetTl;
    
    // Route preferences
    private String transportMode; // "walking", "public_transport", "driving"
    private Boolean mustIncludeCategories;
    private Boolean avoidOvercrowded;
    
    // Context
    private String weather;
    private String currentTime;
    private String dayOfWeek;
    
    // Optional weight overrides
    private Map<String, Double> weightOverrides;
    
    // Constructors
    public RouteOptimizationRequest() {
    }
    
    public RouteOptimizationRequest(
        Long userId,
        Double originLat,
        Double originLng,
        List<Long> candidatePoiIds,
        Integer timeBudgetMinutes,
        Double distanceBudgetKm,
        Double monetaryBudgetTl,
        String transportMode
    ) {
        this.userId = userId;
        this.originLat = originLat;
        this.originLng = originLng;
        this.candidatePoiIds = candidatePoiIds;
        this.timeBudgetMinutes = timeBudgetMinutes;
        this.distanceBudgetKm = distanceBudgetKm;
        this.monetaryBudgetTl = monetaryBudgetTl;
        this.transportMode = transportMode;
    }
    
    // Getters and setters
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Double getOriginLat() {
        return originLat;
    }
    
    public void setOriginLat(Double originLat) {
        this.originLat = originLat;
    }
    
    public Double getOriginLng() {
        return originLng;
    }
    
    public void setOriginLng(Double originLng) {
        this.originLng = originLng;
    }
    
    public List<Long> getCandidatePoiIds() {
        return candidatePoiIds;
    }
    
    public void setCandidatePoiIds(List<Long> candidatePoiIds) {
        this.candidatePoiIds = candidatePoiIds;
    }
    
    public Integer getTimeBudgetMinutes() {
        return timeBudgetMinutes;
    }
    
    public void setTimeBudgetMinutes(Integer timeBudgetMinutes) {
        this.timeBudgetMinutes = timeBudgetMinutes;
    }
    
    public Double getDistanceBudgetKm() {
        return distanceBudgetKm;
    }
    
    public void setDistanceBudgetKm(Double distanceBudgetKm) {
        this.distanceBudgetKm = distanceBudgetKm;
    }
    
    public Double getMonetaryBudgetTl() {
        return monetaryBudgetTl;
    }
    
    public void setMonetaryBudgetTl(Double monetaryBudgetTl) {
        this.monetaryBudgetTl = monetaryBudgetTl;
    }
    
    public String getTransportMode() {
        return transportMode;
    }
    
    public void setTransportMode(String transportMode) {
        this.transportMode = transportMode;
    }
    
    public Boolean getMustIncludeCategories() {
        return mustIncludeCategories;
    }
    
    public void setMustIncludeCategories(Boolean mustIncludeCategories) {
        this.mustIncludeCategories = mustIncludeCategories;
    }
    
    public Boolean getAvoidOvercrowded() {
        return avoidOvercrowded;
    }
    
    public void setAvoidOvercrowded(Boolean avoidOvercrowded) {
        this.avoidOvercrowded = avoidOvercrowded;
    }
    
    public String getWeather() {
        return weather;
    }
    
    public void setWeather(String weather) {
        this.weather = weather;
    }
    
    public String getCurrentTime() {
        return currentTime;
    }
    
    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
    
    public String getDayOfWeek() {
        return dayOfWeek;
    }
    
    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }
    
    public Map<String, Double> getWeightOverrides() {
        return weightOverrides;
    }
    
    public void setWeightOverrides(Map<String, Double> weightOverrides) {
        this.weightOverrides = weightOverrides;
    }
}
