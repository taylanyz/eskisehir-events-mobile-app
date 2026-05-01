package com.eskisehir.eventapi.service.nlp;

import com.eskisehir.eventapi.domain.model.FeedbackTheme;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashSet;
import java.util.Set;

/**
 * Turkish sentiment analyzer using lexicon-based MVP approach.
 * Analyzes free-form Turkish feedback text for sentiment and complaint themes.
 * 
 * MVP Strategy:
 * - Dictionary lookup (no deep learning required)
 * - Handles basic negation and intensifiers
 * - Emoji and exclamation mark heuristics
 * - Fast (<200ms) and reproducible
 */
@Service
public class TurkishSentimentAnalyzer {
    
    private static final Logger log = LoggerFactory.getLogger(TurkishSentimentAnalyzer.class);
    private final TurkishSentimentLexicon lexicon;
    private final FeedbackThemeExtractor themeExtractor;
    
    public TurkishSentimentAnalyzer(TurkishSentimentLexicon lexicon, FeedbackThemeExtractor themeExtractor) {
        this.lexicon = lexicon;
        this.themeExtractor = themeExtractor;
    }
    
    /**
     * Analyze Turkish text for sentiment.
     * Returns score in [-1, 1] where -1 = very negative, 0 = neutral, 1 = very positive.
     */
    public SentimentAnalysis analyzeSentiment(String turkishText) {
        if (turkishText == null || turkishText.isBlank()) {
            log.debug("Empty feedback text provided");
            return new SentimentAnalysis(0.0, Set.of());
        }
        
        long startTime = System.currentTimeMillis();
        
        // 1. Preprocess
        String normalized = preprocess(turkishText);
        String[] tokens = normalized.split("\\s+");
        
        // 2. Score tokens with sentiment words
        double totalScore = 0.0;
        int sentimentTokenCount = 0;
        
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            Double sentimentValue = null;
            
            // Check if positive or negative word
            if (lexicon.POSITIVE_WORDS.containsKey(token)) {
                sentimentValue = lexicon.POSITIVE_WORDS.get(token);
            } else if (lexicon.NEGATIVE_WORDS.containsKey(token)) {
                sentimentValue = lexicon.NEGATIVE_WORDS.get(token);
            }
            
            if (sentimentValue != null) {
                // Check for negation in previous 3 words
                boolean negated = false;
                for (int j = Math.max(0, i - 3); j < i; j++) {
                    if (lexicon.NEGATION_WORDS.contains(tokens[j])) {
                        negated = true;
                        break;
                    }
                }
                
                if (negated) {
                    sentimentValue = -sentimentValue;  // Flip sign
                }
                
                // Check for intensifiers/diminishers in previous word
                if (i > 0) {
                    String previous = tokens[i - 1];
                    if (lexicon.INTENSIFIERS.containsKey(previous)) {
                        sentimentValue *= lexicon.INTENSIFIERS.get(previous);
                    } else if (lexicon.DIMINISHERS.containsKey(previous)) {
                        sentimentValue *= lexicon.DIMINISHERS.get(previous);
                    }
                }
                
                totalScore += sentimentValue;
                sentimentTokenCount++;
            }
        }
        
        // 3. Emoji sentiment bonus
        double emojiBonus = 0.0;
        if (turkishText.contains("😊") || turkishText.contains("😄") || turkishText.contains("👍") ||
            turkishText.contains("❤️") || turkishText.contains("😍")) {
            emojiBonus = 0.2;
        } else if (turkishText.contains("😠") || turkishText.contains("😞") || turkishText.contains("👎") ||
                   turkishText.contains("😡") || turkishText.contains("🤬")) {
            emojiBonus = -0.2;
        }
        
        // 4. Exclamation mark intensity
        int exclamationCount = (int) turkishText.chars().filter(ch -> ch == '!').count();
        double exclamationBonus = Math.min(exclamationCount * 0.1, 0.3);
        if (totalScore < 0) {
            exclamationBonus = -exclamationBonus;  // Flip sign if negative text
        }
        
        // 5. Final score calculation
        double finalScore = 0.0;
        if (sentimentTokenCount > 0) {
            finalScore = totalScore / sentimentTokenCount;  // Average
        }
        finalScore += emojiBonus + exclamationBonus;
        
        // Clamp to [-1, 1]
        finalScore = Math.max(-1.0, Math.min(1.0, finalScore));
        
        // 6. Extract themes
        Set<FeedbackTheme> themes = themeExtractor.extractThemes(turkishText);
        
        long elapsed = System.currentTimeMillis() - startTime;
        log.debug("Sentiment analysis completed in {}ms: score={}, themes={}", 
            elapsed, String.format("%.2f", finalScore), themes);
        
        return new SentimentAnalysis(finalScore, themes);
    }
    
    /**
     * Preprocess Turkish text: lowercase, remove URLs, normalize whitespace.
     */
    private String preprocess(String text) {
        // 1. Lowercase
        String result = text.toLowerCase();
        
        // 2. Remove URLs and email-like patterns
        result = result.replaceAll("https?://\\S+", "");
        result = result.replaceAll("\\S+@\\S+", "");
        
        // 3. Remove extra punctuation but keep basic sentence structure
        result = result.replaceAll("[\\p{P}&&[^!.?]]", "");
        
        // 4. Multiple spaces → single space
        result = result.replaceAll("\\s+", " ");
        
        return result.trim();
    }
}
