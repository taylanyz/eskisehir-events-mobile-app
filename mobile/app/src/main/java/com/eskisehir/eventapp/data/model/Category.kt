package com.eskisehir.eventapp.data.model

/**
 * Enum representing event categories available in the application.
 */
enum class Category(val displayNameTr: String) {
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
    OTHER("Diğer")
}
