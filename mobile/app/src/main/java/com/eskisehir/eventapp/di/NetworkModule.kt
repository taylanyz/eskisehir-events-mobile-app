package com.eskisehir.eventapp.di

import android.content.Context
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.data.local.TokenStore
import com.eskisehir.eventapp.data.remote.AuthApi
import com.eskisehir.eventapp.data.remote.AuthInterceptor
import com.eskisehir.eventapp.data.remote.ErrorHandlingInterceptor
import com.eskisehir.eventapp.data.remote.InteractionApi
import com.eskisehir.eventapp.data.remote.RecommendationApi
import com.eskisehir.eventapp.data.remote.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AuthClientQualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class BaseClientQualifier

/**
 * Hilt Dependency Injection module for network and authentication setup.
 * Provides Retrofit instances, OkHttp client, and token management.
 * Two OkHttpClient instances are provided: one without auth (for AuthApi),
 * and one with auth interceptor (for other authenticated APIs).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://192.168.1.100:8080/api/"  // Change to your backend URL

    @Provides
    @Singleton
    fun provideTokenStore(@ApplicationContext context: Context): TokenStore {
        return TokenStore(context)
    }

    @Provides
    @Singleton
    fun provideTokenManager(tokenStore: TokenStore): TokenManager {
        return TokenManager(tokenStore)
    }

    @Provides
    @Singleton
    fun provideErrorHandlingInterceptor(): ErrorHandlingInterceptor {
        return ErrorHandlingInterceptor()
    }

    @Provides
    @Singleton
    @BaseClientQualifier
    fun provideOkHttpClientBase(
        errorHandlingInterceptor: ErrorHandlingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(errorHandlingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(@BaseClientQualifier httpClient: OkHttpClient): AuthApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        tokenManager: TokenManager,
        authApi: AuthApi
    ): AuthInterceptor {
        return AuthInterceptor(tokenManager, authApi)
    }

    @Provides
    @Singleton
    @AuthClientQualifier
    fun provideOkHttpClientAuth(
        authInterceptor: AuthInterceptor,
        errorHandlingInterceptor: ErrorHandlingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(errorHandlingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(@AuthClientQualifier httpClientWithAuth: OkHttpClient): UserApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecommendationApi(@AuthClientQualifier httpClientWithAuth: OkHttpClient): RecommendationApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RecommendationApi::class.java)
    }

    @Provides
    @Singleton
    fun provideInteractionApi(@AuthClientQualifier httpClientWithAuth: OkHttpClient): InteractionApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClientWithAuth)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InteractionApi::class.java)
    }
}
