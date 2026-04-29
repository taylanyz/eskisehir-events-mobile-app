package com.eskisehir.eventapi.domain.model;

/**
 * Types of user interactions with POIs.
 * Each type maps to a reward value for the bandit algorithm.
 */
public enum InteractionType {
    VIEW(0.1),
    CLICK(0.2),
    SHARE(0.3),
    SAVE(0.4),
    ADD_TO_ROUTE(0.6),
    VISITED(0.8),
    POSITIVE_FEEDBACK(1.0),
    DISLIKE(-0.4);

    private final double rewardValue;

    InteractionType(double rewardValue) {
        this.rewardValue = rewardValue;
    }

    /**
     * Returns the reward value used by the contextual bandit algorithm.
     */
    public double getRewardValue() {
        return rewardValue;
    }
}
