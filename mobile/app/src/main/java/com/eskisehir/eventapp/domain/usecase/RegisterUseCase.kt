package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.model.AuthResponse
import com.eskisehir.eventapp.data.model.RegisterRequest
import com.eskisehir.eventapp.data.remote.AuthApi
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for user registration.
 */
class RegisterUseCase @Inject constructor(
    private val authApi: AuthApi
) {
    suspend operator fun invoke(email: String, displayName: String, password: String): Result<AuthResponse> {
        return try {
            val request = RegisterRequest(email, displayName, password)
            val response = authApi.register(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e, "Registration failed: ${e.message}")
        }
    }
}
