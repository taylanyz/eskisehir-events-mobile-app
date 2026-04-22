package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores contextual bandit events for the LinUCB algorithm.
 * Each record represents: in context X, action A was taken, reward R was observed.
 */
@Entity
@Table(name = "bandit_events")
public class BanditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

    /**
     * Context vector as JSON string.
     * Example: {"weather":"sunny","timeOfDay":"AFTERNOON","dayOfWeek":"SATURDAY",
     *           "budgetLevel":"MEDIUM","crowdPref":"LOW","transportMode":"WALKING"}
     */
    @Column(length = 1000)
    private String contextVectorJson;

    /** The reward signal from the user's interaction */
    private Double reward;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public BanditEvent() {
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Poi getPoi() { return poi; }
    public void setPoi(Poi poi) { this.poi = poi; }

    public String getContextVectorJson() { return contextVectorJson; }
    public void setContextVectorJson(String contextVectorJson) { this.contextVectorJson = contextVectorJson; }

    public Double getReward() { return reward; }
    public void setReward(Double reward) { this.reward = reward; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
