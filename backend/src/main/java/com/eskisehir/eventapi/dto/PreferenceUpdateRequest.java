package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.Category;
import com.eskisehir.eventapi.domain.model.SensitivityLevel;
import com.eskisehir.eventapi.domain.model.MobilityPreference;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

/**
 * Request DTO for updating user preferences.
 */
public class PreferenceUpdateRequest {

    private List<Category> preferredCategories;
    private List<String> preferredTags;
    private SensitivityLevel budgetSensitivity;
    private SensitivityLevel crowdTolerance;
    private MobilityPreference mobilityPreference;

    @Min(value = 0, message = "Sustainability preference must be between 0 and 1")
    @Max(value = 1, message = "Sustainability preference must be between 0 and 1")
    private Double sustainabilityPreference;

    @Min(value = 1, message = "Max walking minutes must be at least 1")
    @Max(value = 600, message = "Max walking minutes cannot exceed 600")
    private Integer maxWalkingMinutes;

    public PreferenceUpdateRequest() {}

    public List<Category> getPreferredCategories() {
        return preferredCategories;
    }

    public void setPreferredCategories(List<Category> preferredCategories) {
        this.preferredCategories = preferredCategories;
    }

    public List<String> getPreferredTags() {
        return preferredTags;
    }

    public void setPreferredTags(List<String> preferredTags) {
        this.preferredTags = preferredTags;
    }

    public SensitivityLevel getBudgetSensitivity() {
        return budgetSensitivity;
    }

    public void setBudgetSensitivity(SensitivityLevel budgetSensitivity) {
        this.budgetSensitivity = budgetSensitivity;
    }

    public SensitivityLevel getCrowdTolerance() {
        return crowdTolerance;
    }

    public void setCrowdTolerance(SensitivityLevel crowdTolerance) {
        this.crowdTolerance = crowdTolerance;
    }

    public MobilityPreference getMobilityPreference() {
        return mobilityPreference;
    }

    public void setMobilityPreference(MobilityPreference mobilityPreference) {
        this.mobilityPreference = mobilityPreference;
    }

    public Double getSustainabilityPreference() {
        return sustainabilityPreference;
    }

    public void setSustainabilityPreference(Double sustainabilityPreference) {
        this.sustainabilityPreference = sustainabilityPreference;
    }

    public Integer getMaxWalkingMinutes() {
        return maxWalkingMinutes;
    }

    public void setMaxWalkingMinutes(Integer maxWalkingMinutes) {
        this.maxWalkingMinutes = maxWalkingMinutes;
    }
}
