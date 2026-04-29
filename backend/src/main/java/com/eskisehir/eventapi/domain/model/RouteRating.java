package com.eskisehir.eventapi.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * User rating and review for a shared route.
 * Supports 5-star rating and optional comment.
 */
@Entity
@Table(name = "route_ratings", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"route_id", "user_id"})
})
public class RouteRating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Double rating; // 1-5 stars

    @Column(length = 500)
    private String comment; // Optional review comment

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public RouteRating() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public RouteRating(Route route, User user, Double rating, String comment) {
        this.route = route;
        this.user = user;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        this.updatedAt = LocalDateTime.now();
    }

    public String getComment() { return comment; }
    public void setComment(String comment) {
        this.comment = comment;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
