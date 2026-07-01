package com.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taher.beatly.ui.auth.ProfileSuccessScreen
import com.taher.beatly.ui.auth.signIn.SignInScreen
import com.taher.beatly.ui.auth.signup.SignUpScreen
import com.taher.beatly.ui.onboarding.OnboardingScreen
import com.taher.beatly.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object ProfileSuccess : Screen("profile_success")
    // data object Home         : Screen("home")
}

@Composable
fun BeatlyNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        // ── Splash ─────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ─────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueFinished = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onRegisterClicked = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        // ── Sign In ────────────────────────────────────────────────────────
        composable(Screen.SignIn.route) {
            SignInScreen(
                onBackClicked = { navController.popBackStack() },
                onSignInSuccess = {
                    navController.navigate(Screen.ProfileSuccess.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onForgotPassword = { /* TODO: ForgotPassword screen */ },
                onRegisterClicked = { navController.navigate(Screen.SignUp.route) },
                onAppleSignIn = { /* TODO: Apple OAuth */ },
                onFacebookSignIn = { /* TODO: Facebook OAuth */ }
            )
        }

        // ── Sign Up ────────────────────────────────────────────────────────
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClicked = { navController.popBackStack() },
                onSignUpSuccess = {
                    navController.navigate(Screen.ProfileSuccess.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onTermsClicked = { /* TODO: Terms screen */ },
                onPrivacyClicked = { /* TODO: Privacy screen */ }
            )
        }

        // ── Profile Setup Success ──────────────────────────────────────────
        composable(Screen.ProfileSuccess.route) {
            ProfileSuccessScreen(
                onContinue = {
                    // navController.navigate(Screen.Home.route) {
                    //     popUpTo(0) { inclusive = true }
                    // }
                },
                onCallSupport = { /* TODO: open support */ }
            )
        }
    }
}