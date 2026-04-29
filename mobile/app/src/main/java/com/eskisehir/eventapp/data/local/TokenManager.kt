package com.eskisehir.eventapp.data.local

import com.eskisehir.eventapp.data.model.AuthResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

/**
 * TokenManager provides high-level token management operations.
 * Wraps TokenStore and provides convenient methods for authentication flow.
 */
class TokenManager(private val tokenStore: TokenStore) {

    val accessTokenFlow: Flow<String?> = tokenStore.accessTokenFlow
    val refreshTokenFlow: Flow<String?> = tokenStore.refreshTokenFlow
    val userIdFlow: Flow<String?> = tokenStore.userIdFlow
    val emailFlow: Flow<String?> = tokenStore.emailFlow
    val displayNameFlow: Flow<String?> = tokenStore.displayNameFlow

    suspend fun saveTokens(authResponse: AuthResponse) {
        tokenStore.saveTokens(
            accessToken = authResponse.accessToken,
            refreshToken = authResponse.refreshToken,
            userId = authResponse.userId.toString(),
            email = authResponse.email,
            displayName = authResponse.displayName
        )
    }

    suspend fun getAccessToken(): String? {
        return tokenStore.getAccessToken()
    }

    suspend fun getRefreshToken(): String? = tokenStore.refreshTokenFlow.firstOrNull()

    suspend fun updateAccessToken(accessToken: String) {
        tokenStore.updateAccessToken(accessToken)
    }

    suspend fun clearTokens() {
        tokenStore.clearTokens()
    }

    suspend fun isUserLoggedIn(): Boolean {
        return getAccessToken() != null
    }
}
