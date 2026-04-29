package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.RouteRating;
import java.time.LocalDateTime;

/**
 * Response DTO for route ratings, including user information.
 */
public class RouteRatingResponse {
    private Long id;
    private Double rating;
    private String comment;
    private String userName; // Display name of the rater
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public RouteRatingResponse() {}

    public RouteRatingResponse(RouteRating rating) {
        this.id = rating.getId();
        this.rating = rating.getRating();
        this.comment = rating.getComment();
        this.userName = rating.getUser().getDisplayName();
        this.createdAt = rating.getCreatedAt();
        this.updatedAt = rating.getUpdatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
