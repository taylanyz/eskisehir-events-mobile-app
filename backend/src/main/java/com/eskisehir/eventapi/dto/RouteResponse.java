package com.eskisehir.eventapi.dto;

import java.util.List;

/**
 * Response DTO for optimized routes.
 * Contains ordered POIs and route metadata (distance, time, cost).
 */
public class RouteResponse {

    private List<PoiResponse> orderedPois;
    private Double totalDistanceKm;
    private Integer totalWalkingMinutes;
    private Double estimatedCostTRY;
    private String routeStatus;  // "FEASIBLE", "PARTIAL", "NOT_FEASIBLE"

    public RouteResponse() {}

    public RouteResponse(List<PoiResponse> orderedPois, Double totalDistanceKm,
                       Integer totalWalkingMinutes, Double estimatedCostTRY, String routeStatus) {
        this.orderedPois = orderedPois;
        this.totalDistanceKm = totalDistanceKm;
        this.totalWalkingMinutes = totalWalkingMinutes;
        this.estimatedCostTRY = estimatedCostTRY;
        this.routeStatus = routeStatus;
    }

    public List<PoiResponse> getOrderedPois() { return orderedPois; }
    public void setOrderedPois(List<PoiResponse> orderedPois) { this.orderedPois = orderedPois; }
    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }
    public Integer getTotalWalkingMinutes() { return totalWalkingMinutes; }
    public void setTotalWalkingMinutes(Integer totalWalkingMinutes) { this.totalWalkingMinutes = totalWalkingMinutes; }
    public Double getEstimatedCostTRY() { return estimatedCostTRY; }
    public void setEstimatedCostTRY(Double estimatedCostTRY) { this.estimatedCostTRY = estimatedCostTRY; }
    public String getRouteStatus() { return routeStatus; }
    public void setRouteStatus(String routeStatus) { this.routeStatus = routeStatus; }
}
