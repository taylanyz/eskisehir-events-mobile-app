package com.eskisehir.eventapi.dto;

/**
 * Aggregated metrics for a generated route.
 */
public class RouteMetrics {
    private Double totalDistanceKm;
    private Integer totalDurationMinutes;
    private Double totalCostTl;
    private Double co2EmissionsKg;
    private Integer localPoiCount;
    private Integer totalPoiCount;
    
    public RouteMetrics() {
    }
    
    public RouteMetrics(
        Double totalDistanceKm,
        Integer totalDurationMinutes,
        Double totalCostTl,
        Double co2EmissionsKg,
        Integer localPoiCount,
        Integer totalPoiCount
    ) {
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationMinutes = totalDurationMinutes;
        this.totalCostTl = totalCostTl;
        this.co2EmissionsKg = co2EmissionsKg;
        this.localPoiCount = localPoiCount;
        this.totalPoiCount = totalPoiCount;
    }
    
    // Getters and setters
    public Double getTotalDistanceKm() {
        return totalDistanceKm;
    }
    
    public void setTotalDistanceKm(Double totalDistanceKm) {
        this.totalDistanceKm = totalDistanceKm;
    }
    
    public Integer getTotalDurationMinutes() {
        return totalDurationMinutes;
    }
    
    public void setTotalDurationMinutes(Integer totalDurationMinutes) {
        this.totalDurationMinutes = totalDurationMinutes;
    }
    
    public Double getTotalCostTl() {
        return totalCostTl;
    }
    
    public void setTotalCostTl(Double totalCostTl) {
        this.totalCostTl = totalCostTl;
    }
    
    public Double getCo2EmissionsKg() {
        return co2EmissionsKg;
    }
    
    public void setCo2EmissionsKg(Double co2EmissionsKg) {
        this.co2EmissionsKg = co2EmissionsKg;
    }
    
    public Integer getLocalPoiCount() {
        return localPoiCount;
    }
    
    public void setLocalPoiCount(Integer localPoiCount) {
        this.localPoiCount = localPoiCount;
    }
    
    public Integer getTotalPoiCount() {
        return totalPoiCount;
    }
    
    public void setTotalPoiCount(Integer totalPoiCount) {
        this.totalPoiCount = totalPoiCount;
    }
}
