package com.eskisehir.eventapi.service.feedback;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import com.eskisehir.eventapi.domain.model.UserFeedback;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;
import java.util.HashSet;

/**
 * Maps user feedback (star rating + sentiment) to reward score for Thompson Sampling.
 * 
 * Formula:
 * - normalizedStars = (starRating - 1) / 4  → [0, 1]
 * - normalizedSentiment = (sentimentScore + 1) / 2  → [0, 1]
 * - weightedScore = 0.6 × normalizedStars + 0.4 × normalizedSentiment
 * - confidenceBoost = 1.2 if feedbackText present, else 1.0
 * - finalReward = min(weightedScore × confidenceBoost, 1.0)
 */
@Service
public class FeedbackRewardMapper {
    
    private static final Logger log = LoggerFactory.getLogger(FeedbackRewardMapper.class);
    
    /**
     * Map user feedback to reward score for bandit learning.
     */
    public FeedbackReward mapFeedbackToReward(UserFeedback feedback) {
        // 1. Normalize star rating to [0, 1]
        Double normalizedStars = (feedback.getStarRating() - 1.0) / 4.0;
        
        // 2. Normalize sentiment to [0, 1]
        Double sentimentScore = feedback.getSentimentScore();
        if (sentimentScore == null) {
            // No text: derive sentiment purely from stars
            sentimentScore = (feedback.getStarRating() - 1.0) / 4.0 * 2.0 - 1.0;  // Map [1,5] → [-1, 1]
        }
        Double normalizedSentiment = (sentimentScore + 1.0) / 2.0;
        
        // 3. Weighted combination: stars (60%) + sentiment (40%)
        Double weightedScore = 0.6 * normalizedStars + 0.4 * normalizedSentiment;
        
        // 4. Confidence boost if text provided
        Double confidenceBoost = 1.0;
        if (feedback.getFeedbackText() != null && !feedback.getFeedbackText().isBlank()) {
            confidenceBoost = 1.2;
        }
        
        Double finalReward = Math.min(weightedScore * confidenceBoost, 1.0);
        
        // 5. Theme-based penalty for critical issues
        if (feedback.getThemes() != null && !feedback.getThemes().isEmpty()) {
            if (feedback.getThemes().contains(FeedbackTheme.SAFETY_CONCERN)) {
                // Safety concerns are critical → cap reward
                finalReward = Math.min(finalReward, 0.2);
                log.info("Safety concern detected in feedback {}: reward capped to 0.2", feedback.getId());
            }
        }
        
        log.debug("Feedback {} mapped to reward: stars={}, sentiment={:.2f}, weighted={:.2f}, final={:.2f}",
            feedback.getId(), feedback.getStarRating(), sentimentScore, weightedScore, finalReward);
        
        return new FeedbackReward(
            feedback.getId(),
            feedback.getUser().getId(),
            feedback.getRoute() != null ? feedback.getRoute().getId() : null,
            finalReward,
            feedback.getThemes() != null ? feedback.getThemes() : new HashSet<>()
        );
    }
}
