package com.eskisehir.eventapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.eskisehir.eventapp.data.local.TokenManager

/**
 * Root navigation composable that conditionally shows auth or app navigation
 * based on authentication state.
 */
@Composable
fun RootNavigation(tokenManager: TokenManager) {
    val navController = rememberNavController()
    
    // Check if user is logged in
    val accessToken by tokenManager.accessTokenFlow.collectAsState(initial = null)
    val isLoggedIn = accessToken != null

    NavHost(
        navController = navController,
        startDestination = if (isLoggedIn) "app" else "auth"
    ) {
        authNavGraph(navController)
        appNavGraph(navController)
    }
}
