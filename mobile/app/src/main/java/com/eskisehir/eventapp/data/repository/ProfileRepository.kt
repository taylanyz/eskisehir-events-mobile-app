package com.eskisehir.eventapp.data.repository

import com.eskisehir.eventapp.data.local.dao.UserProfileDAO
import com.eskisehir.eventapp.data.local.entity.UserProfileEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepository @Inject constructor(
    private val userProfileDAO: UserProfileDAO
) {
    private val gson = Gson()

    fun getUserProfile(userId: String): Flow<UserProfileEntity?> =
        userProfileDAO.getUserProfile(userId)

    suspend fun saveInterestAreas(userId: String, interests: List<String>) {
        val existing = userProfileDAO.getUserProfileOnce(userId)
        val json = gson.toJson(interests)
        userProfileDAO.upsertUserProfile(
            (existing ?: UserProfileEntity(userId = userId)).copy(
                interestAreas = json,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveProfileImageUri(userId: String, uri: String) {
        val existing = userProfileDAO.getUserProfileOnce(userId)
        userProfileDAO.upsertUserProfile(
            (existing ?: UserProfileEntity(userId = userId)).copy(
                profileImageUri = uri,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    fun parseInterests(json: String): List<String> {
        return if (json.isEmpty()) emptyList()
        else gson.fromJson(json, object : TypeToken<List<String>>() {}.type)
    }
}
