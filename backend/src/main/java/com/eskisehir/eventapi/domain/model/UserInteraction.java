package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Logs a user's interaction with a POI.
 * Used for recommendation learning (contextual bandit rewards)
 * and analytics.
 */
@Entity
@Table(name = "user_interactions")
public class UserInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poi_id", nullable = false)
    private Poi poi;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InteractionType interactionType;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    // Contextual features captured at interaction time
    private String contextWeather;
    private String contextTimeOfDay;   // MORNING, AFTERNOON, EVENING, NIGHT
    private String contextDayOfWeek;

    public UserInteraction() {
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Poi getPoi() { return poi; }
    public void setPoi(Poi poi) { this.poi = poi; }

    public InteractionType getInteractionType() { return interactionType; }
    public void setInteractionType(InteractionType interactionType) { this.interactionType = interactionType; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getContextWeather() { return contextWeather; }
    public void setContextWeather(String contextWeather) { this.contextWeather = contextWeather; }

    public String getContextTimeOfDay() { return contextTimeOfDay; }
    public void setContextTimeOfDay(String contextTimeOfDay) { this.contextTimeOfDay = contextTimeOfDay; }

    public String getContextDayOfWeek() { return contextDayOfWeek; }
    public void setContextDayOfWeek(String contextDayOfWeek) { this.contextDayOfWeek = contextDayOfWeek; }
}
