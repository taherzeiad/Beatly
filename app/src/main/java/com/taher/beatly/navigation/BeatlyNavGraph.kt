package com.taher.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.taher.beatly.ui.auth.ProfileSuccessScreen
import com.taher.beatly.ui.auth.RecoverySuccessScreen
import com.taher.beatly.ui.auth.recoveryemail.RecoveryEmailSentScreen
import com.taher.beatly.ui.auth.resetpassword.ResetPasswordScreen
import com.taher.beatly.ui.auth.signIn.SignInScreen
import com.taher.beatly.ui.auth.signup.SignUpScreen
import com.taher.beatly.ui.onboarding.OnboardingScreen
import com.taher.beatly.ui.splash.SplashScreen

sealed class Screen(val route: String) {
    data object Splash              : Screen("splash")
    data object Onboarding          : Screen("onboarding")
    data object SignIn              : Screen("sign_in")
    data object SignUp              : Screen("sign_up")
    data object ProfileSuccess      : Screen("profile_success")
    data object ForgotPassword      : Screen("forgot_password")
    data object RecoveryEmailSent   : Screen("recovery_email_sent")
    data object ResetPassword       : Screen("reset_password")
    data object RecoverySuccess     : Screen("recovery_success")
    // data object Home             : Screen("home")
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
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        // ── Sign In ────────────────────────────────────────────────────────
        composable(Screen.SignIn.route) {
            SignInScreen(
                onBackClicked     = { navController.popBackStack() },
                onSignInSuccess   = {
                    navController.navigate(Screen.ProfileSuccess.route) {
                        popUpTo(Screen.SignIn.route) { inclusive = true }
                    }
                },
                onForgotPassword  = { navController.navigate(Screen.ForgotPassword.route) },
                onRegisterClicked = { navController.navigate(Screen.SignUp.route) },
                onAppleSignIn     = { /* TODO */ },
                onFacebookSignIn  = { /* TODO */ }
            )
        }

        // ── Sign Up ────────────────────────────────────────────────────────
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClicked    = { navController.popBackStack() },
                onSignUpSuccess  = {
                    navController.navigate(Screen.ProfileSuccess.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                },
                onTermsClicked   = { /* TODO */ },
                onPrivacyClicked = { /* TODO */ }
            )
        }

        // ── Profile Setup Success ──────────────────────────────────────────
        composable(Screen.ProfileSuccess.route) {
            ProfileSuccessScreen(
                onContinue    = { /* TODO: navigate to Home */ },
                onCallSupport = { /* TODO */ }
            )
        }

        // ── Forgot Password ────────────────────────────────────────────────
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onContinue    = { navController.navigate(Screen.RecoveryEmailSent.route) },
                onCallSupport = { /* TODO */ }
            )
        }

        // ── Recovery Email Sent ────────────────────────────────────────────
        composable(Screen.RecoveryEmailSent.route) {
            RecoveryEmailSentScreen(
                onContinue    = { navController.navigate(Screen.ResetPassword.route) },
                onCallSupport = { /* TODO */ }
            )
        }

        // ── Reset Password ─────────────────────────────────────────────────
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onContinue    = {
                    navController.navigate(Screen.RecoverySuccess.route) {
                        popUpTo(Screen.ForgotPassword.route) { inclusive = true }
                    }
                },
                onCallSupport = { /* TODO */ }
            )
        }

        // ── Recovery Success ───────────────────────────────────────────────
        composable(Screen.RecoverySuccess.route) {
            RecoverySuccessScreen(
                onContinue    = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCallSupport = { /* TODO */ }
            )
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onBackClicked: () -> Boolean,
    onContinue: () -> Unit,
    onCallSupport: () -> Unit
) {
    TODO("Not yet implemented")
}