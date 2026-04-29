package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.model.RefreshTokenRequest
import com.eskisehir.eventapp.data.remote.AuthApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for token refresh.
 */
class RefreshTokenUseCase @Inject constructor(
    private val authApi: AuthApi
) {
    suspend operator fun invoke(refreshToken: String): Result<AuthResponse> {
        return try {
            val request = RefreshTokenRequest(refreshToken)
            val response = authApi.refreshToken(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Token refresh failed: ${e.message}")
        }
    }
}
