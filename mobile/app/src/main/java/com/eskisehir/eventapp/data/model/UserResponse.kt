package com.eskisehir.eventapp.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response DTO for user information.
 */
data class UserResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("email")
    val email: String,
    @SerializedName("displayName")
    val displayName: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("lastLoginAt")
    val lastLoginAt: String? = null
)
