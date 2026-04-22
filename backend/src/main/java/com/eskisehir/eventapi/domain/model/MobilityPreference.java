package com.eskisehir.eventapi.domain.model;

/**
 * User's preferred mode of transportation.
 * Affects route optimization constraints and carbon footprint calculation.
 */
public enum MobilityPreference {
    WALKING("Yürüyüş"),
    PUBLIC_TRANSPORT("Toplu Taşıma"),
    CAR("Araba"),
    BIKE("Bisiklet");

    private final String displayNameTr;

    MobilityPreference(String displayNameTr) {
        this.displayNameTr = displayNameTr;
    }

    public String getDisplayNameTr() {
        return displayNameTr;
    }
}
