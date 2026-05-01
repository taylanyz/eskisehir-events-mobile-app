package com.eskisehir.eventapi.domain.model;

/**
 * Categorized themes/complaint areas extracted from user feedback.
 * Used to identify systemic issues with recommendations and routes.
 */
public enum FeedbackTheme {
    ROUTE_QUALITY("Rota Kalitesi"),
    CROWDING("Kalabalık"),
    BUDGET_EXCEEDED("Bütçe Aşıldı"),
    TIME_MISMATCH("Zaman Uyumsuzluğu"),
    BORING("Sıkıcı"),
    ACCESSIBILITY("Erişim"),
    SAFETY_CONCERN("Güvenlik Sorunu"),
    QUALITY_OF_EXPERIENCE("Genel Yaşam Kalitesi");

    private final String displayNameTr;

    FeedbackTheme(String displayNameTr) {
        this.displayNameTr = displayNameTr;
    }

    public String getDisplayNameTr() {
        return displayNameTr;
    }
}
