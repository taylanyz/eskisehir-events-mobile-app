package com.eskisehir.eventapi.domain.model;

/**
 * Reusable sensitivity/tolerance level.
 * Used for budget sensitivity and crowd tolerance in user preferences.
 */
public enum SensitivityLevel {
    LOW("Düşük"),
    MEDIUM("Orta"),
    HIGH("Yüksek");

    private final String displayNameTr;

    SensitivityLevel(String displayNameTr) {
        this.displayNameTr = displayNameTr;
    }

    public String getDisplayNameTr() {
        return displayNameTr;
    }
}
