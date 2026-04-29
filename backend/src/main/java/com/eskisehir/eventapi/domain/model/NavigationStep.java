package com.eskisehir.eventapi.domain.model;

import java.util.List;

/**
 * Represents a single navigation step in a route.
 * Used for turn-by-turn directions.
 */
public class NavigationStep {
    private Long fromPoiId;
    private Long toPoiId;
    private String instruction;
    private Double distanceKm;
    private Integer durationMinutes;
    private Double startLatitude;
    private Double startLongitude;
    private Double endLatitude;
    private Double endLongitude;
    private Integer stepNumber;

    public NavigationStep(Long fromPoiId, Long toPoiId, String instruction,
                         Double distanceKm, Integer durationMinutes,
                         Double startLatitude, Double startLongitude,
                         Double endLatitude, Double endLongitude,
                         Integer stepNumber) {
        this.fromPoiId = fromPoiId;
        this.toPoiId = toPoiId;
        this.instruction = instruction;
        this.distanceKm = distanceKm;
        this.durationMinutes = durationMinutes;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.stepNumber = stepNumber;
    }

    // Getters
    public Long getFromPoiId() { return fromPoiId; }
    public Long getToPoiId() { return toPoiId; }
    public String getInstruction() { return instruction; }
    public Double getDistanceKm() { return distanceKm; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public Double getStartLatitude() { return startLatitude; }
    public Double getStartLongitude() { return startLongitude; }
    public Double getEndLatitude() { return endLatitude; }
    public Double getEndLongitude() { return endLongitude; }
    public Integer getStepNumber() { return stepNumber; }
}
