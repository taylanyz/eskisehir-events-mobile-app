package com.eskisehir.eventapp.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * TokenStore manages secure token storage using DataStore.
 * All token-related data is stored encrypted on device.
 */
class TokenStore(private val context: Context) {

    companion object {
        private const val TOKEN_STORE_NAME = "auth_tokens"
        private val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        private val USER_ID_KEY = stringPreferencesKey("user_id")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val DISPLAY_NAME_KEY = stringPreferencesKey("display_name")
    }

    private val dataStore: DataStore<Preferences> by preferencesDataStore(name = TOKEN_STORE_NAME)

    val accessTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN_KEY]
    }

    val refreshTokenFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN_KEY]
    }

    val userIdFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[USER_ID_KEY]
    }

    val emailFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[EMAIL_KEY]
    }

    val displayNameFlow: Flow<String?> = dataStore.data.map { preferences ->
        preferences[DISPLAY_NAME_KEY]
    }

    suspend fun saveTokens(
        accessToken: String,
        refreshToken: String,
        userId: String,
        email: String,
        displayName: String
    ) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId
            preferences[EMAIL_KEY] = email
            preferences[DISPLAY_NAME_KEY] = displayName
        }
    }

    suspend fun updateAccessToken(accessToken: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN_KEY] = accessToken
        }
    }

    suspend fun clearTokens() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getAccessToken(): String? {
        val preferences = dataStore.data.map { it[ACCESS_TOKEN_KEY] }
        return preferences.map { it }.first { true }
    }
}

private val Context.tokenStore: DataStore<Preferences> by preferencesDataStore(name = "auth_tokens")
