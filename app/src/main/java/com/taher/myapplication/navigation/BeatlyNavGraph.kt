package com.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.beatly.ui.onboarding.OnboardingScreen
import com.beatly.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    // Add more screens here: Login, Register, Home, etc.
}

@Composable
fun BeatlyNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueFinished = {
                    // Navigate to Login or Home screen
                    // navController.navigate(Screen.Login.route)
                },
                onRegisterClicked = {
                    // Navigate to Register screen
                    // navController.navigate(Screen.Register.route)
                }
            )
        }
    }
}