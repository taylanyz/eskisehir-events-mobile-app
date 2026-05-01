package com.eskisehir.eventapi.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;

/**
 * DTO for recording user feedback (visited confirmation + rating/comment).
 */
public class UserFeedbackRequest {

    @NotNull
    private Long userId;

    @NotNull
    private Long poiId;

    @Min(1)
    @Max(5)
    private Integer rating;

    private String feedback;

    @NotNull
    private Boolean visited;

    public UserFeedbackRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPoiId() { return poiId; }
    public void setPoiId(Long poiId) { this.poiId = poiId; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Boolean getVisited() { return visited; }
    public void setVisited(Boolean visited) { this.visited = visited; }
}
