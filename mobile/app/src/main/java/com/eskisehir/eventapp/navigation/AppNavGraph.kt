package com.eskisehir.eventapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.eskisehir.eventapp.ui.screens.profile.ProfileScreen

/**
 * Main app navigation graph for authenticated users.
 * Contains Home, Explore, Favorites, Profile, and other app screens.
 */
fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Home.route, route = "app") {
        composable(Screen.Home.route) {
            // Home screen - to be implemented
            // HomeScreen()
        }
        
        composable(Screen.Explore.route) {
            // Explore screen - to be implemented
            // ExploreScreen()
        }
        
        composable(Screen.Favorites.route) {
            // Favorites screen - to be implemented
            // FavoritesScreen()
        }
        
        composable(Screen.Profile.route) {
            ProfileScreen()
        }
        
        composable(Screen.Preferences.route) {
            // Preferences screen - to be implemented
            // PreferencesScreen()
        }
        
        composable(Screen.EventDetail.route) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId")
            // EventDetailScreen(eventId = eventId?.toLongOrNull() ?: 0L)
        }
    }
}
