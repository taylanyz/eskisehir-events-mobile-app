package com.eskisehir.eventapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "favorite_events",
    primaryKeys = ["userId", "eventId"],
    indices = [Index(value = ["userId"])]
)
data class FavoriteEventEntity(
    val userId: String,
    val eventId: Long,
    val addedAt: Long = System.currentTimeMillis()
)
