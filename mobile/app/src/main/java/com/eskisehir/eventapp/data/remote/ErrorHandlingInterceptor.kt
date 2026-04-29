package com.eskisehir.eventapp.data.remote

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

/**
 * ErrorHandlingInterceptor logs HTTP responses for debugging.
 * Useful for tracking API errors and network issues.
 */
class ErrorHandlingInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val startTime = System.currentTimeMillis()

        val response = try {
            chain.proceed(request)
        } catch (e: Exception) {
            Log.e(TAG, "Network request failed: ${request.url}", e)
            throw e
        }

        val duration = System.currentTimeMillis() - startTime
        Log.d(TAG, "${request.method} ${request.url} - Status: ${response.code} (${duration}ms)")

        if (!response.isSuccessful) {
            Log.w(TAG, "HTTP Error: ${response.code} ${response.message}")
        }

        return response
    }

    companion object {
        private const val TAG = "ErrorHandlingInterceptor"
    }
}
