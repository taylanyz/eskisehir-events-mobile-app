package com.eskisehir.eventapp.domain.usecase

import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.domain.Result
import javax.inject.Inject

/**
 * Use case for user logout.
 * Clears all local tokens.
 */
class LogoutUseCase @Inject constructor(
    private val tokenManager: TokenManager
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            tokenManager.clearTokens()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e, "Logout failed: ${e.message}")
        }
    }
}
