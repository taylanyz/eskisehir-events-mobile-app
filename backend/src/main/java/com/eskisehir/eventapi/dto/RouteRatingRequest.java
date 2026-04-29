package com.eskisehir.eventapi.dto;

/**
 * Request DTO for submitting or updating a route rating.
 */
public class RouteRatingRequest {
    private Double rating; // 1-5 stars
    private String comment; // Optional review comment

    public RouteRatingRequest() {}

    public RouteRatingRequest(Double rating, String comment) {
        this.rating = rating;
        this.comment = comment;
    }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
