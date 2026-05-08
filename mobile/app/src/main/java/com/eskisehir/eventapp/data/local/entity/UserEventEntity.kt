package com.eskisehir.eventapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "user_events",
    primaryKeys = ["userId", "eventId"],
    indices = [Index(value = ["userId"]), Index(value = ["eventId"])]
)
data class UserEventEntity(
    val userId: String,
    val eventId: Long,
    val status: String, // ATTENDED, GOING, WANT_TO_GO
    val updatedAt: Long = System.currentTimeMillis()
)
