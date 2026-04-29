package com.eskisehir.eventapi.dto;

import java.util.List;

public class RouteRequest {

    private List<Long> eventIds;
    private Double startLatitude;
    private Double startLongitude;
    private Integer durationMinutes;  // Total planned duration in minutes (e.g., 120 for 2 hours)
    private Integer maxWalkingMinutes;  // Max walking time between POIs
    private Double maxBudget;  // Maximum budget in TRY
    private List<String> preferredCategories;  // Filter by category
    private String mobilityPreference;  // e.g., "WALKING", "PUBLIC_TRANSPORT"

    public RouteRequest() {}

    public List<Long> getEventIds() { return eventIds; }
    public void setEventIds(List<Long> eventIds) { this.eventIds = eventIds; }
    public Double getStartLatitude() { return startLatitude; }
    public void setStartLatitude(Double startLatitude) { this.startLatitude = startLatitude; }
    public Double getStartLongitude() { return startLongitude; }
    public void setStartLongitude(Double startLongitude) { this.startLongitude = startLongitude; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public Integer getMaxWalkingMinutes() { return maxWalkingMinutes; }
    public void setMaxWalkingMinutes(Integer maxWalkingMinutes) { this.maxWalkingMinutes = maxWalkingMinutes; }
    public Double getMaxBudget() { return maxBudget; }
    public void setMaxBudget(Double maxBudget) { this.maxBudget = maxBudget; }
    public List<String> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(List<String> preferredCategories) { this.preferredCategories = preferredCategories; }
    public String getMobilityPreference() { return mobilityPreference; }
    public void setMobilityPreference(String mobilityPreference) { this.mobilityPreference = mobilityPreference; }
}
