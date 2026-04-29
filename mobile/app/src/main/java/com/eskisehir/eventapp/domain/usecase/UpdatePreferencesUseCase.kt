package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.PreferenceUpdateRequest
import com.eskisehir.eventapp.data.model.UserResponse
import com.eskisehir.eventapp.data.remote.UserApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for updating user preferences.
 */
class UpdatePreferencesUseCase @Inject constructor(
    private val userApi: UserApi
) {
    suspend operator fun invoke(request: PreferenceUpdateRequest): Result<UserResponse> {
        return try {
            val response = userApi.updatePreferences(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Failed to update preferences: ${e.message}")
        }
    }
}
