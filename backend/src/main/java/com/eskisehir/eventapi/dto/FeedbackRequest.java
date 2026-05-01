package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * Request DTO for submitting user feedback on completed routes.
 * Captures quantitative (star rating) and qualitative (free text) feedback.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Route ID is required")
    private Long routeId;
    
    @NotNull(message = "Star rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer starRating;
    
    @Size(max = 500, message = "Feedback text must not exceed 500 characters")
    private String feedbackText;  // Optional Turkish text
    
    private Set<FeedbackTheme> suggestedThemes;  // Optional hints from mobile UI
    
    // Getters
    public Long getUserId() {
        return userId;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public Integer getStarRating() {
        return starRating;
    }
    
    public String getFeedbackText() {
        return feedbackText;
    }
    
    public Set<FeedbackTheme> getSuggestedThemes() {
        return suggestedThemes;
    }
    
    // Setters
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public void setStarRating(Integer starRating) {
        this.starRating = starRating;
    }
    
    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }
    
    public void setSuggestedThemes(Set<FeedbackTheme> suggestedThemes) {
        this.suggestedThemes = suggestedThemes;
    }
}
