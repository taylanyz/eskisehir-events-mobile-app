package com.eskisehir.eventapi.controller;

import com.eskisehir.eventapi.dto.UserInteractionRequest;
import com.eskisehir.eventapi.dto.UserFeedbackRequest;
import com.eskisehir.eventapi.service.BanditLearningService;
import com.eskisehir.eventapi.service.InteractionService;
import com.eskisehir.eventapi.ml.ContextVectorBuilder;
import com.eskisehir.eventapi.domain.model.User;
import com.eskisehir.eventapi.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Interaction Tracking Controller: Records user interactions for learning.
 *
 * Endpoints:
 * - POST /api/interactions : Record view, click, save, add_to_route interactions
 * - POST /api/feedback : Record visit confirmation and user feedback
 */
@RestController
@RequestMapping("/api/interactions")
@CrossOrigin(origins = "*")
public class InteractionTrackingController {

    private static final Logger log = LoggerFactory.getLogger(InteractionTrackingController.class);

    private final BanditLearningService banditLearningService;
    private final InteractionService interactionService;
    private final ContextVectorBuilder contextVectorBuilder;
    private final UserRepository userRepository;

    public InteractionTrackingController(
            BanditLearningService banditLearningService,
            InteractionService interactionService,
            ContextVectorBuilder contextVectorBuilder,
            UserRepository userRepository) {
        this.banditLearningService = banditLearningService;
        this.interactionService = interactionService;
        this.contextVectorBuilder = contextVectorBuilder;
        this.userRepository = userRepository;
    }

    /**
     * Record a user interaction (view, click, save, add_to_route).
     *
     * Request body:
     * {
     *   "userId": 123,
     *   "poiId": 456,
     *   "interactionType": "click",
     *   "timeOfDay": "AFTERNOON",
     *   "dayOfWeek": "SATURDAY",
     *   "latitude": 39.76,
     *   "longitude": 30.52
     * }
     */
    @PostMapping
    public ResponseEntity<Void> recordInteraction(@Valid @RequestBody UserInteractionRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            // Build context vector for bandit learning
            String contextJson = contextVectorBuilder.buildContextVectorJson(user, request.toRecommendationRequest());

            // Record bandit event for learning
            banditLearningService.recordInteraction(
                    request.getUserId(),
                    request.getPoiId(),
                    request.getInteractionType(),
                    contextJson
            );

            log.info("Recorded interaction: user={}, poi={}, type={}", 
                    request.getUserId(), request.getPoiId(), request.getInteractionType());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error recording interaction", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Record user feedback (visited confirmation + rating/comment).
     *
     * Request body:
     * {
     *   "userId": 123,
     *   "poiId": 456,
     *   "rating": 5,
     *   "feedback": "Çok güzel bir yer! Mutlaka tekrar ziyaret edeceğim.",
     *   "visited": true
     * }
     */
    @PostMapping("/feedback")
    public ResponseEntity<Void> recordFeedback(@Valid @RequestBody UserFeedbackRequest request) {
        try {
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            User user = userOpt.get();

            // Determine interaction type based on rating
            String interactionType = "VISITED";
            if (request.getRating() != null) {
                if (request.getRating() >= 5) {
                    interactionType = "POSITIVE_FEEDBACK";
                } else if (request.getRating() <= 2) {
                    interactionType = "DISLIKE";
                } else {
                    interactionType = "VISITED";
                }
            }

            // Build context for bandit learning
            String contextJson = contextVectorBuilder.buildContextVectorJson(user, null);

            // Record as bandit event
            banditLearningService.recordInteraction(
                    request.getUserId(),
                    request.getPoiId(),
                    interactionType,
                    contextJson
            );

            log.info("Recorded feedback: user={}, poi={}, rating={}, visited={}",
                    request.getUserId(), request.getPoiId(), request.getRating(), request.getVisited());

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error recording feedback", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
