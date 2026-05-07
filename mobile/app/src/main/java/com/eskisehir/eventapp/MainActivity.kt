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
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
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
            val lightColors = lightColorScheme(
                primary = Color(0xFF4CAF50),           // Yeşil
                onPrimary = Color.White,
                primaryContainer = Color(0xFFC8E6C9), // Açık yeşil
                onPrimaryContainer = Color(0xFF1B5E20),
                secondary = Color(0xFF66BB6A),         // Açık yeşil
                onSecondary = Color.White,
                tertiary = Color(0xFF81C784),          // Çok açık yeşil
                onTertiary = Color.Black,
                background = Color(0xFFE8F5E9),        // Çok açık yeşil arka plan
                onBackground = Color(0xFF1B5E20),      // Koyu yeşil metin
                surface = Color(0xFFF1F8F6),           // Yüzey açık yeşil
                onSurface = Color(0xFF1B5E20),         // Koyu yeşil metin
            )
            
            MaterialTheme(colorScheme = lightColors) {
                // Set status bar to light
                val view = LocalView.current
                if (!view.isInEditMode) {
                    SideEffect {
                        val window = (view.context as MainActivity).window
                        window.statusBarColor = lightColors.primary.copy(alpha = 0.9f).hashCode()
                        WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = true
                    }
                }
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
