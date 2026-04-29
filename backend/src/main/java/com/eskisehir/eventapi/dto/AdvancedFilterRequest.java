package com.eskisehir.eventapi.dto;

import java.util.List;

/**
 * Advanced filter request for POI filtering.
 * Supports price range, distance, rating, and other criteria.
 */
public class AdvancedFilterRequest {
    private Double minPrice;
    private Double maxPrice;
    private Double maxDistance; // in km from latitude/longitude
    private Double latitude;
    private Double longitude;
    private Double minRating; // 0-5
    private List<String> categories;
    private List<String> tags;
    private Boolean onlyOpen; // only show open venues

    public AdvancedFilterRequest() {}

    public Double getMinPrice() { return minPrice; }
    public void setMinPrice(Double minPrice) { this.minPrice = minPrice; }

    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }

    public Double getMaxDistance() { return maxDistance; }
    public void setMaxDistance(Double maxDistance) { this.maxDistance = maxDistance; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getMinRating() { return minRating; }
    public void setMinRating(Double minRating) { this.minRating = minRating; }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public Boolean getOnlyOpen() { return onlyOpen; }
    public void setOnlyOpen(Boolean onlyOpen) { this.onlyOpen = onlyOpen; }
}
