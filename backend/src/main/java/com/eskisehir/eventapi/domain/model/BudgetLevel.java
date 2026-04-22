package com.eskisehir.eventapi.domain.model;

/**
 * Budget level classification for POIs.
 * Used for filtering and recommendation scoring.
 */
public enum BudgetLevel {
    FREE("Ücretsiz"),
    LOW("Düşük"),
    MEDIUM("Orta"),
    HIGH("Yüksek");

    private final String displayNameTr;

    BudgetLevel(String displayNameTr) {
        this.displayNameTr = displayNameTr;
    }

    public String getDisplayNameTr() {
        return displayNameTr;
    }
}
