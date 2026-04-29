package com.eskisehir.eventapp.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.eskisehir.eventapp.ui.screens.auth.LoginScreen
import com.eskisehir.eventapp.ui.screens.auth.RegisterScreen

/**
 * Auth navigation graph for login and registration flows.
 */
fun NavGraphBuilder.authNavGraph(navController: NavHostController) {
    navigation(startDestination = Screen.Login.route, route = "auth") {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("app") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        
        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("app") {
                        popUpTo("auth") { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
