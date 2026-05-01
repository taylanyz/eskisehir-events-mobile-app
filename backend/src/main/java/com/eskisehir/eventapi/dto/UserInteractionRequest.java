package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.MobilityPreference;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for recording user interactions (view, click, save, add_to_route).
 */
public class UserInteractionRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long poiId;

    @NotNull
    private String interactionType;

    private String timeOfDay;
    private String dayOfWeek;
    private Double latitude;
    private Double longitude;
    private MobilityPreference mobilityPreference;

    public UserInteractionRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPoiId() { return poiId; }
    public void setPoiId(Long poiId) { this.poiId = poiId; }

    public String getInteractionType() { return interactionType; }
    public void setInteractionType(String interactionType) { this.interactionType = interactionType; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public MobilityPreference getMobilityPreference() { return mobilityPreference; }
    public void setMobilityPreference(MobilityPreference mobilityPreference) { this.mobilityPreference = mobilityPreference; }

    /**
     * Convert to RecommendationRequest for context vector building.
     */
    public RecommendationRequest toRecommendationRequest() {
        RecommendationRequest request = new RecommendationRequest();
        request.setUserId(this.userId);
        request.setTimeOfDay(this.timeOfDay);
        request.setDayOfWeek(this.dayOfWeek);
        request.setLatitude(this.latitude);
        request.setLongitude(this.longitude);
        request.setMobilityPreference(this.mobilityPreference);
        return request;
    }
}
