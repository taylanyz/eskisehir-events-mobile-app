package com.eskisehir.eventapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "comments",
    indices = [Index(value = ["eventId"])]
)
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val eventId: Long,
    val userId: String,
    val userDisplayName: String,
    val userEmail: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
