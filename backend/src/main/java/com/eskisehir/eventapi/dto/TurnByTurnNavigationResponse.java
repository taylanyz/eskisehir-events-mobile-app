package com.eskisehir.eventapi.dto;

import java.util.List;

/**
 * Response DTO for turn-by-turn navigation.
 */
public class TurnByTurnNavigationResponse {
    private Long routeId;
    private List<NavigationStepDto> steps;
    private Double totalDistanceKm;
    private Integer totalDurationMinutes;
    private Integer currentStepIndex;

    public TurnByTurnNavigationResponse(Long routeId, List<NavigationStepDto> steps,
                                       Double totalDistanceKm, Integer totalDurationMinutes,
                                       Integer currentStepIndex) {
        this.routeId = routeId;
        this.steps = steps;
        this.totalDistanceKm = totalDistanceKm;
        this.totalDurationMinutes = totalDurationMinutes;
        this.currentStepIndex = currentStepIndex;
    }

    // Getters & Setters
    public Long getRouteId() { return routeId; }
    public void setRouteId(Long routeId) { this.routeId = routeId; }

    public List<NavigationStepDto> getSteps() { return steps; }
    public void setSteps(List<NavigationStepDto> steps) { this.steps = steps; }

    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public Integer getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(Integer totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public Integer getCurrentStepIndex() { return currentStepIndex; }
    public void setCurrentStepIndex(Integer currentStepIndex) { this.currentStepIndex = currentStepIndex; }

    /**
     * Inner DTO for individual navigation steps.
     */
    public static class NavigationStepDto {
        private Integer stepNumber;
        private Long fromPoiId;
        private Long toPoiId;
        private String fromPoiName;
        private String toPoiName;
        private String instruction;
        private Double distanceKm;
        private Integer durationMinutes;
        private Double startLatitude;
        private Double startLongitude;
        private Double endLatitude;
        private Double endLongitude;

        public NavigationStepDto(Integer stepNumber, Long fromPoiId, Long toPoiId,
                                String fromPoiName, String toPoiName, String instruction,
                                Double distanceKm, Integer durationMinutes,
                                Double startLatitude, Double startLongitude,
                                Double endLatitude, Double endLongitude) {
            this.stepNumber = stepNumber;
            this.fromPoiId = fromPoiId;
            this.toPoiId = toPoiId;
            this.fromPoiName = fromPoiName;
            this.toPoiName = toPoiName;
            this.instruction = instruction;
            this.distanceKm = distanceKm;
            this.durationMinutes = durationMinutes;
            this.startLatitude = startLatitude;
            this.startLongitude = startLongitude;
            this.endLatitude = endLatitude;
            this.endLongitude = endLongitude;
        }

        // Getters
        public Integer getStepNumber() { return stepNumber; }
        public Long getFromPoiId() { return fromPoiId; }
        public Long getToPoiId() { return toPoiId; }
        public String getFromPoiName() { return fromPoiName; }
        public String getToPoiName() { return toPoiName; }
        public String getInstruction() { return instruction; }
        public Double getDistanceKm() { return distanceKm; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public Double getStartLatitude() { return startLatitude; }
        public Double getStartLongitude() { return startLongitude; }
        public Double getEndLatitude() { return endLatitude; }
        public Double getEndLongitude() { return endLongitude; }
    }
}
