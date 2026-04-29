package com.eskisehir.eventapi.dto;

import com.eskisehir.eventapi.domain.model.InteractionType;
import jakarta.validation.constraints.NotNull;

/**
 * Request payload for logging a user interaction with a POI.
 */
public class InteractionRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotNull(message = "POI ID is required")
    private Long poiId;

    private Long eventId;

    @NotNull(message = "Interaction type is required")
    private InteractionType interactionType;

    private String comment;
    private String weather;
    private String timeOfDay;
    private String dayOfWeek;

    public InteractionRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getPoiId() { return poiId; }
    public void setPoiId(Long poiId) { this.poiId = poiId; }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public InteractionType getInteractionType() { return interactionType; }
    public void setInteractionType(InteractionType interactionType) { this.interactionType = interactionType; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getWeather() { return weather; }
    public void setWeather(String weather) { this.weather = weather; }

    public String getTimeOfDay() { return timeOfDay; }
    public void setTimeOfDay(String timeOfDay) { this.timeOfDay = timeOfDay; }

    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
}
