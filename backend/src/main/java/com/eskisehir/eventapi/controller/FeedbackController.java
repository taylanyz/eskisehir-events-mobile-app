package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.FeedbackRequest;
import com.eskisehir.eventapi.dto.FeedbackResponse;
import com.eskisehir.eventapi.service.feedback.FeedbackService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for user feedback submission (Phase 11).
 * Endpoint: POST /api/feedback
 * 
 * Accepts user star ratings and free-form Turkish feedback on completed routes.
 * Returns sentiment analysis results and integrated reward score.
 */
@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {
    
    private static final Logger log = LoggerFactory.getLogger(FeedbackController.class);
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    /**
     * Submit feedback on a completed route.
     * 
     * Request body:
     * {
     *   "userId": 123,
     *   "routeId": 456,
     *   "starRating": 5,
     *   "feedbackText": "Harika rota, çok iyi planlanmış!", (optional)
     *   "suggestedThemes": ["ROUTE_QUALITY"] (optional)
     * }
     * 
     * Response:
     * {
     *   "feedbackId": 789,
     *   "sentimentScore": 0.85,
     *   "themes": ["ROUTE_QUALITY"],
     *   "mappedRewardScore": 0.92,
     *   "message": "Geri bildiriminiz kaydedildi. Teşekkürler!"
     * }
     */
    @PostMapping
    public ResponseEntity<FeedbackResponse> submitFeedback(
        @Valid @RequestBody FeedbackRequest request
    ) {
        log.info("Feedback submission received: userId={}, routeId={}, starRating={}", 
            request.getUserId(), request.getRouteId(), request.getStarRating());
        
        try {
            FeedbackResponse response = feedbackService.submitFeedback(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid feedback request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error processing feedback submission", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Health check endpoint for feedback service.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Feedback service is running");
    }
}
