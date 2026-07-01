package com.taher.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taher.beatly.ui.auth.signIn.SignInScreen
import com.taher.beatly.ui.auth.signup.SignUpScreen
import com.taher.beatly.ui.onboarding.OnboardingScreen
import com.taher.beatly.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash     : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object SignIn     : Screen("sign_in")
    data object SignUp     : Screen("sign_up")
}

@Composable
fun BeatlyNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
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
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Sign In ────────────────────────────────────────────────────────
        composable(Screen.SignIn.route) {
            SignInScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onSignInSuccess = {
                    // navController.navigate(Screen.Home.route) {
                    //     popUpTo(Screen.SignIn.route) { inclusive = true }
                    // }
                },
                onForgotPassword = {
                    // navController.navigate(Screen.ForgotPassword.route)
                },
                onRegisterClicked = {
                    navController.navigate(Screen.SignUp.route)
                },
                onAppleSignIn = {
                    // handle Apple OAuth
                },
                onFacebookSignIn = {
                    // handle Facebook OAuth
                }
            )
        }

        // ── Sign Up ────────────────────────────────────────────────────────
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClicked = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    // navController.navigate(Screen.Home.route)
                },
                onTermsClicked = {
                    // open Terms
                },
                onPrivacyClicked = {
                    // open Privacy
                }
            )
        }
    }
}