package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.UserResponse
import com.eskisehir.eventapp.data.remote.UserApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for getting current user information.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val userApi: UserApi
) {
    suspend operator fun invoke(): Result<UserResponse> {
        return try {
            val response = userApi.getCurrentUser()
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Failed to get current user: ${e.message}")
        }
    }
}
