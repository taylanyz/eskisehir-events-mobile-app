package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.MobilityPreference;
import java.util.List;

/**
 * Request DTO for the recommendation endpoint.
 * Contains user preferences for POI scoring and contextual metadata.
 */
public class RecommendationRequest {
    private List<Category> preferredCategories;

    private Long userId;
    private List<String> preferredTags;
    private Double maxPrice;
    private Integer limit;
    private Double latitude;
    private Double longitude;
    private String timeOfDay;
    private String dayOfWeek;
    private MobilityPreference mobilityPreference;

    public RecommendationRequest() {}

    public List<Category> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(List<Category> preferredCategories) { this.preferredCategories = preferredCategories; }

    public List<String> getPreferredTags() { return preferredTags; }
    public void setPreferredTags(List<String> preferredTags) { this.preferredTags = preferredTags; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }

    public MobilityPreference getMobilityPreference() { return mobilityPreference; }
    public void setMobilityPreference(MobilityPreference mobilityPreference) { this.mobilityPreference = mobilityPreference; }

    public int getEffectiveLimit() {
        return (limit != null && limit > 0) ? limit : 10;
    }
}
