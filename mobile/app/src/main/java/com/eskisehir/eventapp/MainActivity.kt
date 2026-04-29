package com.eskisehir.eventapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.android.AndroidEntryPoint
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eskisehir.eventapp.data.local.TokenManager
import com.eskisehir.eventapp.navigation.RootNavigation
import com.eskisehir.eventapp.navigation.Screen
import com.eskisehir.eventapp.ui.screens.detail.EventDetailScreen
import com.eskisehir.eventapp.ui.screens.explore.ExploreScreen
import com.eskisehir.eventapp.ui.screens.favorites.FavoritesScreen
import com.eskisehir.eventapp.ui.screens.home.HomeScreen
import com.eskisehir.eventapp.ui.screens.profile.ProfileScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var tokenManager: TokenManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                RootNavigation(tokenManager)
            }
        }
    }
}

/*
 * Legacy code - kept for reference
 * Bottom navigation now handled in AppNavGraph
 */

/** Bottom navigation tab definition */
/*
data class BottomNavItem(val screen: Screen, val icon: ImageVector, val label: String)

val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, Icons.Default.Home, "Ana Sayfa"),
    BottomNavItem(Screen.Explore, Icons.Default.Explore, "Keşfet"),
    BottomNavItem(Screen.Favorites, Icons.Default.Favorite, "Favoriler"),
    BottomNavItem(Screen.Profile, Icons.Default.Person, "Profil")
)

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Show bottom bar only on main tabs (not on detail screen)
    val showBottomBar = bottomNavItems.any { it.screen.route == currentDestination?.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any {
                                it.route == item.screen.route
                            } == true,
                            onClick = {
                                navController.navigate(item.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onEventClick = { eventId ->
                        navController.navigate(Screen.EventDetail.createRoute(eventId))
                    }
                )
            }
            composable(Screen.Explore.route) {
                ExploreScreen(
                    onEventClick = { eventId ->
                        navController.navigate(Screen.EventDetail.createRoute(eventId))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen()
            }
            composable(Screen.Profile.route) {
                ProfileScreen()
            }
            composable(
                route = Screen.EventDetail.route,
                arguments = listOf(navArgument("eventId") { type = NavType.LongType })
            ) { backStackEntry ->
                val eventId = backStackEntry.arguments?.getLong("eventId") ?: 0L
                EventDetailScreen(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}
*/
