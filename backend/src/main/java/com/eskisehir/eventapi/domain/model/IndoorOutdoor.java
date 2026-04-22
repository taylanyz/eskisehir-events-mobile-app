package com.eskisehir.eventapi.domain.model;

/**
 * Whether a POI is indoor, outdoor, or both.
 * Relevant for weather-aware recommendations.
 */
public enum IndoorOutdoor {
    INDOOR("İç Mekan"),
    OUTDOOR("Dış Mekan"),
    BOTH("Her İkisi");

    private final String displayNameTr;

    IndoorOutdoor(String displayNameTr) {
        this.displayNameTr = displayNameTr;
    }

    public String getDisplayNameTr() {
        return displayNameTr;
    }
}
