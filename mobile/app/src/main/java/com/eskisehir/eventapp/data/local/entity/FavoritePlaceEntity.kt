package com.eskisehir.eventapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorite_places",
    indices = [Index(value = ["userId"])]
)
data class FavoritePlaceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: String,
    val placeId: String,         // POI id (String UUID)
    val placeName: String,
    val placeAddress: String,
    val placeCategory: String,
    val addedAt: Long = System.currentTimeMillis()
)
