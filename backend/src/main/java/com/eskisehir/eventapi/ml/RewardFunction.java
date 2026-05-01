package com.eskisehir.eventapi.ml;

import org.springframework.stereotype.Service;

/**
 * Reward Function for Thompson Sampling contextual bandit.
 *
 * Maps user interactions to reward signals:
 *   - view            = 0.1  (user sees recommendation, clicks to view details)
 *   - click           = 0.2  (user clicks POI card)
 *   - save            = 0.4  (user saves POI)
 *   - add_to_route    = 0.6  (user adds POI to route)
 *   - visited         = 0.8  (user confirms visit)
 *   - positive_feedback = 1.0 (user gives 5-star rating or positive comment)
 *   - dislike         = -0.4 (user dislikes or gives negative feedback)
 *
 * Reward range: [-0.4, 1.0] -> normalized to [0, 1] for Thompson Sampling.
 */
@Service
public class RewardFunction {

    public static final double REWARD_VIEW = 0.1;
    public static final double REWARD_CLICK = 0.2;
    public static final double REWARD_SAVE = 0.4;
    public static final double REWARD_ADD_TO_ROUTE = 0.6;
    public static final double REWARD_VISITED = 0.8;
    public static final double REWARD_POSITIVE_FEEDBACK = 1.0;
    public static final double REWARD_DISLIKE = -0.4;

    private static final double MIN_REWARD = -0.4;
    private static final double MAX_REWARD = 1.0;
    private static final double REWARD_RANGE = MAX_REWARD - MIN_REWARD;

    /**
     * Get reward for interaction type.
     */
    public double getReward(String interactionType) {
        if (interactionType == null) {
            return 0.0;
        }

        return switch (interactionType.toUpperCase()) {
            case "VIEW" -> REWARD_VIEW;
            case "CLICK" -> REWARD_CLICK;
            case "SAVE" -> REWARD_SAVE;
            case "ADD_TO_ROUTE" -> REWARD_ADD_TO_ROUTE;
            case "VISITED" -> REWARD_VISITED;
            case "POSITIVE_FEEDBACK" -> REWARD_POSITIVE_FEEDBACK;
            case "DISLIKE" -> REWARD_DISLIKE;
            default -> 0.0;
        };
    }

    /**
     * Normalize reward to [0, 1] for Thompson Sampling.
     * Formula: (reward - min) / range
     */
    public double normalize(double reward) {
        return (reward - MIN_REWARD) / REWARD_RANGE;
    }

    /**
     * Denormalize from [0, 1] back to original [-0.4, 1.0] range.
     */
    public double denormalize(double normalizedReward) {
        return (normalizedReward * REWARD_RANGE) + MIN_REWARD;
    }

    /**
     * Reward description for logging and analysis.
     */
    public String describe(String interactionType) {
        return switch (interactionType.toUpperCase()) {
            case "VIEW" -> "User viewed POI details";
            case "CLICK" -> "User clicked on POI";
            case "SAVE" -> "User saved POI for later";
            case "ADD_TO_ROUTE" -> "User added POI to route plan";
            case "VISITED" -> "User confirmed visiting POI";
            case "POSITIVE_FEEDBACK" -> "User gave positive feedback (5-star)";
            case "DISLIKE" -> "User disliked or gave negative feedback";
            default -> "Unknown interaction";
        };
    }
}
