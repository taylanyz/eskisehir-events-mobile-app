package com.eskisehir.eventapp.data.remote

import android.util.Log
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

/**
 * AuthInterceptor handles token injection and refresh logic.
 * - Adds Authorization header to all requests
 * - Handles 401 responses by attempting token refresh
 * - Retries original request with new token on successful refresh
 * - Redirects to login if refresh fails
 */
class AuthInterceptor(
    private val tokenManager: TokenManager,
    private val authApi: AuthApi
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Get current token and add to request
        val token = runBlocking {
            tokenManager.getAccessToken()
        }

        val requestWithToken = if (token != null) {
            originalRequest.addAuthorizationHeader(token)
        } else {
            originalRequest
        }

        var response = chain.proceed(requestWithToken)

        // If 401 Unauthorized, attempt to refresh token
        if (response.code == 401) {
            Log.d(TAG, "Received 401 Unauthorized, attempting token refresh")
            response.close()

            val refreshToken = runBlocking {
                tokenManager.getRefreshToken()
            }

            if (refreshToken != null) {
                val newToken = runBlocking {
                    try {
                        val refreshRequest = RefreshTokenRequest(refreshToken)
                        val authResponse = authApi.refreshToken(refreshRequest)
                        tokenManager.updateAccessToken(authResponse.accessToken)
                        authResponse.accessToken
                    } catch (e: Exception) {
                        Log.e(TAG, "Token refresh failed", e)
                        null
                    }
                }

                if (newToken != null) {
                    // Retry original request with new token
                    val retryRequest = originalRequest.addAuthorizationHeader(newToken)
                    response = chain.proceed(retryRequest)
                } else {
                    // Refresh failed, need to login again
                    Log.w(TAG, "Token refresh failed, user needs to login again")
                    runBlocking {
                        tokenManager.clearTokens()
                    }
                }
            } else {
                Log.w(TAG, "No refresh token available, user needs to login again")
                runBlocking {
                    tokenManager.clearTokens()
                }
            }
        }

        return response
    }

    private fun Request.addAuthorizationHeader(token: String): Request {
        return this.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }

    companion object {
        private const val TAG = "AuthInterceptor"
    }
}
