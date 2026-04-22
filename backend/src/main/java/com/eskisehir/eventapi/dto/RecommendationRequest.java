package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.Category;
import jakarta.validation.constraints.NotNull;
import java.util.List;

/**
 * Request DTO for the recommendation endpoint.
 * Contains user preferences for POI scoring.
 */
public class RecommendationRequest {

    @NotNull(message = "Preferred categories must not be null")
    private List<Category> preferredCategories;

    private List<String> preferredTags;
    private Double maxPrice;
    private Integer limit;

    public RecommendationRequest() {}

    public List<Category> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(List<Category> preferredCategories) { this.preferredCategories = preferredCategories; }
    public List<String> getPreferredTags() { return preferredTags; }
    public void setPreferredTags(List<String> preferredTags) { this.preferredTags = preferredTags; }
    public Double getMaxPrice() { return maxPrice; }
    public void setMaxPrice(Double maxPrice) { this.maxPrice = maxPrice; }
    public Integer getLimit() { return limit; }
    public void setLimit(Integer limit) { this.limit = limit; }

    public int getEffectiveLimit() {
        return (limit != null && limit > 0) ? limit : 10;
    }
}
