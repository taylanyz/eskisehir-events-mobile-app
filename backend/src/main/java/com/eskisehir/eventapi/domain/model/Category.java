package com.eskisehir.eventapi.domain.model;

/**
 * Enum representing event categories available in the application.
 * Each category maps to a type of cultural or entertainment event
 * commonly found in Eskişehir.
 */
public enum Category {
    CONCERT("Konser"),
    THEATER("Tiyatro"),
    EXHIBITION("Sergi"),
    FESTIVAL("Festival"),
    WORKSHOP("Atölye"),
    SPORTS("Spor"),
    STANDUP("Stand-up"),
    CINEMA("Sinema"),
    CONFERENCE("Konferans"),
    CAFE("Kafe"),
    RESTAURANT("Restoran"),
    PARK("Park"),
    MUSEUM("Müze"),
    HISTORICAL("Tarihi Mekan"),
    RIVERSIDE("Nehir Kenarı"),
    CULTURAL("Kültürel"),
    OTHER("Diğer");

    private final String displayNameTr;

    Category(String displayNameTr) {
        this.displayNameTr = displayNameTr;
    }

    /**
     * Returns the Turkish display name for this category.
     */
    public String getDisplayNameTr() {
        return displayNameTr;
    }
}
