package com.eskisehir.eventapp.data.model

/**
 * Reusable sensitivity/tolerance level.
 * Used for budget sensitivity and crowd tolerance in user preferences.
 */
enum class SensitivityLevel(val displayNameTr: String) {
    LOW("Düşük"),
    MEDIUM("Orta"),
    HIGH("Yüksek")
}
