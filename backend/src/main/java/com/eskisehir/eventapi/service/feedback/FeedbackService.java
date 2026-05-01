package com.eskisehir.eventapi.service.feedback;

import com.eskisehir.eventapi.domain.model.Route;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.domain.model.UserFeedback;
import com.eskisehir.eventapi.dto.FeedbackRequest;
import com.eskisehir.eventapi.dto.FeedbackResponse;
import com.eskisehir.eventapi.repository.FeedbackRepository;
import com.eskisehir.eventapi.repository.RouteRepository;
import com.eskisehir.eventapi.repository.UserRepository;
import com.eskisehir.eventapi.service.BanditLearningService;
import com.eskisehir.eventapi.service.nlp.SentimentAnalysis;
import com.eskisehir.eventapi.service.nlp.TurkishSentimentAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Main feedback service orchestrating the full feedback processing pipeline:
 * 1. Persist user feedback
 * 2. Analyze Turkish sentiment
 * 3. Extract themes
 * 4. Map to reward score
 * 5. Update Thompson Sampling via BanditLearningService
 */
@Service
@Transactional
public class FeedbackService {
    
    private static final Logger log = LoggerFactory.getLogger(FeedbackService.class);
    
    private final UserRepository userRepository;
    private final RouteRepository routeRepository;
    private final FeedbackRepository feedbackRepository;
    private final TurkishSentimentAnalyzer sentimentAnalyzer;
    private final FeedbackRewardMapper rewardMapper;
    private final BanditLearningService banditLearningService;
    
    public FeedbackService(
        UserRepository userRepository,
        RouteRepository routeRepository,
        FeedbackRepository feedbackRepository,
        TurkishSentimentAnalyzer sentimentAnalyzer,
        FeedbackRewardMapper rewardMapper,
        BanditLearningService banditLearningService
    ) {
        this.userRepository = userRepository;
        this.routeRepository = routeRepository;
        this.feedbackRepository = feedbackRepository;
        this.sentimentAnalyzer = sentimentAnalyzer;
        this.rewardMapper = rewardMapper;
        this.banditLearningService = banditLearningService;
    }
    
    /**
     * Submit and process user feedback on a completed route.
     * Steps:
     * 1. Validate user and route
     * 2. Analyze Turkish sentiment (if text provided)
     * 3. Extract complaint themes
     * 4. Map to reward score
     * 5. Persist feedback
     * 6. Update bandit learning (async)
     */
    public FeedbackResponse submitFeedback(FeedbackRequest request) {
        log.info("Processing feedback submission for user {} on route {}", 
            request.getUserId(), request.getRouteId());
        
        // 1. Validate user and route exist
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> {
                log.warn("User not found: {}", request.getUserId());
                return new IllegalArgumentException("User not found: " + request.getUserId());
            });
        
        Route route = routeRepository.findById(request.getRouteId())
            .orElseThrow(() -> {
                log.warn("Route not found: {}", request.getRouteId());
                return new IllegalArgumentException("Route not found: " + request.getRouteId());
            });
        
        // 2. Create UserFeedback entity
        UserFeedback feedback = new UserFeedback();
        feedback.setUser(user);
        feedback.setRoute(route);
        feedback.setStarRating(request.getStarRating());
        feedback.setFeedbackText(request.getFeedbackText());
        feedback.setCreatedAtMs(System.currentTimeMillis());
        
        // 3. Analyze sentiment (if text provided)
        if (request.getFeedbackText() != null && !request.getFeedbackText().isBlank()) {
            SentimentAnalysis analysis = sentimentAnalyzer.analyzeSentiment(request.getFeedbackText());
            feedback.setSentimentScore(analysis.getSentimentScore());
            feedback.setThemes(analysis.getThemes());
            log.debug("Sentiment analysis complete: score={}, themes={}", 
                analysis.getSentimentScore(), analysis.getThemes());
        } else if (request.getSuggestedThemes() != null && !request.getSuggestedThemes().isEmpty()) {
            // Use user-provided theme hints if no text
            feedback.setThemes(request.getSuggestedThemes());
            // Derive sentiment from stars alone
            double sentimentFromStars = (request.getStarRating() - 1.0) / 4.0 * 2.0 - 1.0;  // [1,5] → [-1, 1]
            feedback.setSentimentScore(sentimentFromStars);
        } else {
            // No text, no themes: derive sentiment purely from stars
            double sentimentFromStars = (request.getStarRating() - 1.0) / 4.0 * 2.0 - 1.0;  // [1,5] → [-1, 1]
            feedback.setSentimentScore(sentimentFromStars);
        }
        
        // 4. Map to reward score
        FeedbackReward reward = rewardMapper.mapFeedbackToReward(feedback);
        feedback.setMappedRewardScore(reward.getRewardScore());
        
        // 5. Persist
        UserFeedback saved = feedbackRepository.save(feedback);
        log.info("Feedback persisted: id={}, reward={}", saved.getId(), reward.getRewardScore());
        
        // 6. Trigger bandit learning update (will be async or batch later)
        try {
            banditLearningService.recordFeedbackReward(reward);
            saved.setFeedbackProcessed(true);
            feedbackRepository.save(saved);
            log.info("Feedback processed for bandit learning: id={}", saved.getId());
        } catch (Exception e) {
            log.warn("Failed to process feedback for bandit learning (will retry async): {}", e.getMessage());
            // Mark as not processed for later retry
            saved.setFeedbackProcessed(false);
            feedbackRepository.save(saved);
        }
        
        // 7. Build response
        String successMessage = "Geri bildiriminiz kaydedildi. Teşekkürler!";  // "Your feedback was recorded. Thank you!"
        return new FeedbackResponse(
            saved.getId(),
            saved.getSentimentScore(),
            saved.getThemes(),
            saved.getMappedRewardScore(),
            successMessage
        );
    }
}
