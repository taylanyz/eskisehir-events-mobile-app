package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * Response DTO after feedback submission.
 * Includes processed sentiment analysis results and reward score.
 */
@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FeedbackResponse {
    
    private Long feedbackId;
    
    /** Sentiment score computed from Turkish NLP analysis: [-1, 1] */
    private Double sentimentScore;
    
    /** Categorized complaint themes extracted from feedback text */
    private Set<FeedbackTheme> themes;
    
    /** Final reward score for bandit learning: [0, 1] */
    private Double mappedRewardScore;
    
    /** User-facing message (Turkish) */
    private String message;
    
    // Constructor with arguments
    public FeedbackResponse(Long feedbackId, Double sentimentScore, Set<FeedbackTheme> themes, 
                           Double mappedRewardScore, String message) {
        this.feedbackId = feedbackId;
        this.sentimentScore = sentimentScore;
        this.themes = themes;
        this.mappedRewardScore = mappedRewardScore;
        this.message = message;
    }
}
