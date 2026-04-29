package com.eskisehir.eventapp.data.model

data class InteractionRequest(
    val userId: Long,
    val poiId: Long,
    val eventId: Long? = null,
    val interactionType: InteractionType,
    val comment: String? = null,
    val weather: String? = null,
    val timeOfDay: String? = null,
    val dayOfWeek: String? = null
)