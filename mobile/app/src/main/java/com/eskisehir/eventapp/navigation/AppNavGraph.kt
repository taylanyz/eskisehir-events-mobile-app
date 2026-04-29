package com.eskisehir.eventapp.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.eskisehir.eventapp.ui.screens.detail.EventDetailScreen
import com.eskisehir.eventapp.ui.screens.explore.ExploreScreen
import com.eskisehir.eventapp.ui.screens.favorites.FavoritesScreen
import com.eskisehir.eventapp.ui.screens.home.HomeScreen
import com.eskisehir.eventapp.ui.screens.profile.ProfileScreen
import com.eskisehir.eventapp.ui.screens.recommendations.RecommendationsScreen
import com.eskisehir.eventapp.ui.screens.route.RouteGeneratorScreen
import com.eskisehir.eventapp.ui.screens.route.RouteDetailScreen
import com.eskisehir.eventapp.ui.screens.route.NavigationScreen

/**
 * Main app navigation graph for authenticated users.
 * Contains Home, Explore, Recommendations, Favorites, Profile, and detail screens.
 */
fun NavGraphBuilder.appNavGraph(navController: NavHostController) {
    composable("app") {
        AppShell()
    }
}

@Composable
private fun AppShell() {
    val appNavController = rememberNavController()
    val navBackStackEntry by appNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val bottomNavItems = listOf(
        Screen.Home to Icons.Default.Home,
        Screen.Explore to Icons.Default.Explore,
        Screen.Recommendations to Icons.Default.Star,
        Screen.Favorites to Icons.Default.Favorite,
        Screen.Profile to Icons.Default.Person
    )

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomNavItems.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.route) },
                        label = { Text(screen.route.replaceFirstChar { it.uppercase() }) },
                        selected = currentDestination?.route == screen.route,
                        onClick = {
                            appNavController.navigate(screen.route) {
                                popUpTo(appNavController.graph.findStartDestination().id) {
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
    ) { innerPadding ->
        NavHost(
            navController = appNavController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(onEventClick = { eventId ->
                    appNavController.navigate(Screen.EventDetail.createRoute(eventId))
                })
            }
            composable(Screen.Explore.route) {
                ExploreScreen(onEventClick = { eventId ->
                    appNavController.navigate(Screen.EventDetail.createRoute(eventId))
                })
            }
            composable(Screen.Recommendations.route) {
                RecommendationsScreen(onPoiClick = { poiId ->
                    appNavController.navigate(Screen.EventDetail.createRoute(poiId))
                })
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
                    onBackClick = { appNavController.popBackStack() }
                )
            }
            composable(
                route = Screen.RouteGenerator.route,
                arguments = listOf(navArgument("eventIds") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventIdsString = backStackEntry.arguments?.getString("eventIds") ?: ""
                val eventIds = if (eventIdsString.isNotEmpty()) {
                    eventIdsString.split(",").map { it.toLong() }
                } else {
                    emptyList()
                }
                RouteGeneratorScreen(
                    selectedEventIds = eventIds,
                    onRouteGenerated = {
                        appNavController.navigate(Screen.RouteDetail.route) {
                            popUpTo(Screen.RouteGenerator.route) { inclusive = true }
                        }
                    },
                    onBackClick = { appNavController.popBackStack() }
                )
            }
            composable(Screen.RouteDetail.route) {
                RouteDetailScreen(
                    onBackClick = {
                        appNavController.popBackStack()
                    },
                    onPoiClick = { poiId ->
                        appNavController.navigate(Screen.EventDetail.createRoute(poiId))
                    },
                    onStartNavigation = { eventIds ->
                        appNavController.navigate(Screen.Navigation.createRoute(eventIds))
                    }
                )
            }
            composable(
                route = Screen.Navigation.route,
                arguments = listOf(navArgument("eventIds") { type = NavType.StringType })
            ) { backStackEntry ->
                val eventIdsString = backStackEntry.arguments?.getString("eventIds") ?: ""
                val eventIds = if (eventIdsString.isNotEmpty()) {
                    eventIdsString.split(",").map { it.toLong() }
                } else {
                    emptyList()
                }
                NavigationScreen(
                    eventIds = eventIds,
                    onBackClick = { appNavController.popBackStack() }
                )
            }
        }
    }
}
