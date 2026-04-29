package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.model.LoginRequest
import com.eskisehir.eventapp.data.remote.AuthApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for user login.
 */
class LoginUseCase @Inject constructor(
    private val authApi: AuthApi
) {
    suspend operator fun invoke(email: String, password: String): Result<AuthResponse> {
        return try {
            val request = LoginRequest(email, password)
            val response = authApi.login(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Login failed: ${e.message}")
        }
    }
}
