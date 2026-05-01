package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * User feedback after completing a route (Phase 11 - Turkish NLP Feedback Loop).
 * Captures both quantitative (star rating) and qualitative (sentiment from text) signals
 * for iterative learning system improvement.
 */
@Entity
@Table(name = "user_feedback")
public class UserFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    /** Star rating 1-5 (explicit user intent) */
    @Column(nullable = false)
    private Integer starRating;

    /** Legacy field for compatibility */
    @Column(nullable = false)
    private Integer rating;

    /** Free-text feedback in Turkish (max 500 chars in DTO, up to 2000 here) */
    @Column(length = 2000)
    private String feedbackText;

    /** Legacy field for compatibility */
    @Column(length = 2000)
    private String commentText;

    /** Computed sentiment from Turkish NLP analyzer: [-1, 1] */
    private Double sentimentScore;

    /** Categorized complaint themes extracted from feedbackText */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "feedback_themes", joinColumns = @JoinColumn(name = "feedback_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "theme")
    private Set<FeedbackTheme> themes = new HashSet<>();

    /** Final reward score for Thompson Sampling: [0, 1] */
    private Double mappedRewardScore;

    /** Flag for async processing idempotency */
    private Boolean feedbackProcessed = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** Timestamp in milliseconds (for consistency with bandit events) */
    private Long createdAtMs;

    public UserFeedback() {
        this.createdAt = LocalDateTime.now();
        this.createdAtMs = System.currentTimeMillis();
    }

    public UserFeedback(User user, Route route, Integer starRating) {
        this.user = user;
        this.route = route;
        this.starRating = starRating;
        this.rating = starRating;  // Keep legacy field in sync
        this.createdAt = LocalDateTime.now();
        this.createdAtMs = System.currentTimeMillis();
        this.feedbackProcessed = false;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public Integer getStarRating() { return starRating; }
    public void setStarRating(Integer starRating) { 
        this.starRating = starRating;
        this.rating = starRating;  // Keep legacy field in sync
    }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { 
        this.rating = rating;
        this.starRating = rating;  // Keep new field in sync
    }

    public String getFeedbackText() { return feedbackText; }
    public void setFeedbackText(String feedbackText) { 
        this.feedbackText = feedbackText;
        this.commentText = feedbackText;  // Keep legacy field in sync
    }

    public String getCommentText() { return commentText; }
    public void setCommentText(String commentText) { 
        this.commentText = commentText;
        this.feedbackText = commentText;  // Keep new field in sync
    }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }

    public Set<FeedbackTheme> getThemes() { return themes; }
    public void setThemes(Set<FeedbackTheme> themes) { this.themes = themes; }

    public Double getMappedRewardScore() { return mappedRewardScore; }
    public void setMappedRewardScore(Double mappedRewardScore) { this.mappedRewardScore = mappedRewardScore; }

    public Boolean getFeedbackProcessed() { return feedbackProcessed; }
    public void setFeedbackProcessed(Boolean feedbackProcessed) { this.feedbackProcessed = feedbackProcessed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getCreatedAtMs() { return createdAtMs; }
    public void setCreatedAtMs(Long createdAtMs) { this.createdAtMs = createdAtMs; }
}
