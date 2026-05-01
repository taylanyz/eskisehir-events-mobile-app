package com.eskisehir.eventapi.service.nlp;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Set;

/**
 * Result of Turkish sentiment analysis.
 * Contains sentiment score and extracted themes/complaint categories.
 */
@Data
@NoArgsConstructor
public class SentimentAnalysis {
    
    /** Sentiment score: [-1, 1] where -1 = very negative, 0 = neutral, 1 = very positive */
    private Double sentimentScore;
    
    /** Extracted complaint themes (e.g., CROWDING, BUDGET_EXCEEDED) */
    private Set<FeedbackTheme> themes;
    
    // Constructor with arguments
    public SentimentAnalysis(Double sentimentScore, Set<FeedbackTheme> themes) {
        this.sentimentScore = sentimentScore;
        this.themes = themes;
    }
    
    // Getters
    public Double getSentimentScore() {
        return sentimentScore;
    }
    
    public Set<FeedbackTheme> getThemes() {
        return themes;
    }
    
    // Setters
    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }
    
    public void setThemes(Set<FeedbackTheme> themes) {
        this.themes = themes;
    }
}
