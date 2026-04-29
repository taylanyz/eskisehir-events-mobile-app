package com.eskisehir.eventapp.navigation

/**
 * Sealed class defining all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    // Auth routes
    object Login : Screen("login")
    object Register : Screen("register")
    
    // Main app routes
    object Home : Screen("home")
    object Explore : Screen("explore")
    object Recommendations : Screen("recommendations")
    object Favorites : Screen("favorites")
    object Profile : Screen("profile")
    object EventDetail : Screen("event_detail/{eventId}") {
        fun createRoute(eventId: Long) = "event_detail/$eventId"
    }
    object Preferences : Screen("preferences")
    
    // Phase 4: Route planning
    object RouteGenerator : Screen("route_generator/{eventIds}") {
        fun createRoute(eventIds: List<Long>) = "route_generator/${eventIds.joinToString(",")}"
    }
    object RouteDetail : Screen("route_detail")
    object Navigation : Screen("navigation/{eventIds}") {
        fun createRoute(eventIds: List<Long>) = "navigation/${eventIds.joinToString(",")}"
    }
}
