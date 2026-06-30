package com.beatly.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    // Add more screens here: Login, Register, Home, etc.
}
