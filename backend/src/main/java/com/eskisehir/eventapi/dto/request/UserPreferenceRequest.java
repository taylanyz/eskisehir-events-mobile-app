package com.eskisehir.eventapi.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * User preference update request DTO.
 */
@Data
public class UserPreferenceRequest {

    @NotNull(message = "Budget sensitivity is required")
    @JsonProperty("budget_sensitivity")
    private String budgetSensitivity;

    @NotNull(message = "Crowd tolerance is required")
    @JsonProperty("crowd_tolerance")
    private String crowdTolerance;

    @NotNull(message = "Mobility preference is required")
    @JsonProperty("mobility_preference")
    private String mobilityPreference;

    @Min(value = 0, message = "Max walking minutes cannot be negative")
    @Max(value = 480, message = "Max walking minutes cannot exceed 480")
    @JsonProperty("max_walking_minutes")
    private Integer maxWalkingMinutes;

    @DecimalMin(value = "0.0", message = "Sustainability preference must be between 0 and 1")
    @DecimalMax(value = "1.0", message = "Sustainability preference must be between 0 and 1")
    @JsonProperty("sustainability_preference")
    private Double sustainabilityPreference;
}
