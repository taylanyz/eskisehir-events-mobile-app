package com.eskisehir.eventapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val userId: String,
    val interestAreas: String = "",       // JSON array as string
    val profileImageUri: String = "",     // local URI string
    val updatedAt: Long = System.currentTimeMillis()
)
