package com.eskisehir.eventapi.service.feedback;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * DTO representing the result of feedback-to-reward mapping.
 * Used internally by FeedbackService to pass reward info to BanditLearningService.
 */
@Data
@NoArgsConstructor
public class FeedbackReward {
    private Long feedbackId;
    private Long userId;
    private Long routeId;
    private Double rewardScore;           // [0, 1] for Thompson Sampling
    private Set<FeedbackTheme> themes;   // Identified complaint areas
    
    // Constructor with arguments
    public FeedbackReward(Long feedbackId, Long userId, Long routeId, Double rewardScore, Set<FeedbackTheme> themes) {
        this.feedbackId = feedbackId;
        this.userId = userId;
        this.routeId = routeId;
        this.rewardScore = rewardScore;
        this.themes = themes;
    }
    
    // Getters
    public Long getFeedbackId() {
        return feedbackId;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public Long getRouteId() {
        return routeId;
    }
    
    public Double getRewardScore() {
        return rewardScore;
    }
    
    public Set<FeedbackTheme> getThemes() {
        return themes;
    }
    
    // Setters
    public void setFeedbackId(Long feedbackId) {
        this.feedbackId = feedbackId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }
    
    public void setRewardScore(Double rewardScore) {
        this.rewardScore = rewardScore;
    }
    
    public void setThemes(Set<FeedbackTheme> themes) {
        this.themes = themes;
    }
}
