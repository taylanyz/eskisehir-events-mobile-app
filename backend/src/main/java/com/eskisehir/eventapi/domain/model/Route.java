package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * A generated or saved route consisting of ordered POI visits.
 * Supports social features: sharing, ratings, and trending.
 */
@Entity
@Table(name = "routes")
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    private Double totalDistanceKm;
    private Integer totalDurationMinutes;
    private Double estimatedBudget;
    private Double carbonScore;

    @Enumerated(EnumType.STRING)
    private MobilityPreference transportMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RouteStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Social Features
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isPublic = false;

    @Column(unique = true)
    private String shareCode; // UUID for sharing routes

    @Column(columnDefinition = "FLOAT DEFAULT 0.0")
    private Double averageRating = 0.0; // Average rating (0-5)

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer totalRatings = 0; // Total number of ratings

    @Column(columnDefinition = "INTEGER DEFAULT 0")
    private Integer shareCount = 0; // Number of times shared

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("visitOrder ASC")
    private List<RouteItem> items;

    public Route() {
        this.createdAt = LocalDateTime.now();
        this.status = RouteStatus.PLANNED;
        this.isPublic = false;
        this.averageRating = 0.0;
        this.totalRatings = 0;
        this.shareCount = 0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getTotalDistanceKm() { return totalDistanceKm; }
    public void setTotalDistanceKm(Double totalDistanceKm) { this.totalDistanceKm = totalDistanceKm; }

    public Integer getTotalDurationMinutes() { return totalDurationMinutes; }
    public void setTotalDurationMinutes(Integer totalDurationMinutes) { this.totalDurationMinutes = totalDurationMinutes; }

    public Double getEstimatedBudget() { return estimatedBudget; }
    public void setEstimatedBudget(Double estimatedBudget) { this.estimatedBudget = estimatedBudget; }

    public Double getCarbonScore() { return carbonScore; }
    public void setCarbonScore(Double carbonScore) { this.carbonScore = carbonScore; }

    public MobilityPreference getTransportMode() { return transportMode; }
    public void setTransportMode(MobilityPreference transportMode) { this.transportMode = transportMode; }

    public RouteStatus getStatus() { return status; }
    public void setStatus(RouteStatus status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<RouteItem> getItems() { return items; }
    public void setItems(List<RouteItem> items) { this.items = items; }

    // Social Features Getters/Setters
    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getShareCode() { return shareCode; }
    public void setShareCode(String shareCode) { this.shareCode = shareCode; }

    public void generateShareCode() {
        this.shareCode = UUID.randomUUID().toString();
    }

    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }

    public Integer getTotalRatings() { return totalRatings; }
    public void setTotalRatings(Integer totalRatings) { this.totalRatings = totalRatings; }

    public Integer getShareCount() { return shareCount; }
    public void setShareCount(Integer shareCount) { this.shareCount = shareCount; }

    /**
     * Update average rating when a new rating is added.
     */
    public void addRating(Double rating) {
        if (rating < 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        }
        double totalScore = averageRating * totalRatings + rating;
        totalRatings++;
        averageRating = totalScore / totalRatings;
    }

    /**
     * Increment share count.
     */
    public void incrementShareCount() {
        this.shareCount++;
    }
}
