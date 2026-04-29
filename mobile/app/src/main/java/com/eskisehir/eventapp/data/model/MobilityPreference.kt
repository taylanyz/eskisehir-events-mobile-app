package com.eskisehir.eventapp.data.model

/**
 * User's preferred mode of transportation.
 * Affects route optimization constraints and carbon footprint calculation.
 */
enum class MobilityPreference(val displayNameTr: String) {
    WALKING("Yürüyüş"),
    PUBLIC_TRANSPORT("Toplu Taşıma"),
    CAR("Araba"),
    BIKE("Bisiklet")
}
