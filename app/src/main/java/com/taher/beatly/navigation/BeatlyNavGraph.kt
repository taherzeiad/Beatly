package com.taher.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.beatly.ui.profile.ProfileScreen
import com.taher.beatly.ui.auth.*
import com.taher.beatly.ui.auth.forgotpassword.ForgotPasswordScreen
import com.taher.beatly.ui.auth.recoveryemail.RecoveryEmailSentScreen
import com.taher.beatly.ui.auth.resetpassword.ResetPasswordScreen
import com.taher.beatly.ui.auth.signIn.SignInScreen
import com.taher.beatly.ui.auth.signup.SignUpScreen
import com.taher.beatly.ui.components.BeatlyTab
import com.taher.beatly.ui.home.HomeScreen
import com.taher.beatly.ui.onboarding.OnboardingScreen
import com.taher.beatly.ui.settings.*
import com.taher.beatly.ui.splash.SplashScreen
import com.taher.beatly.ui.subscription.*

sealed class Screen(val route: String) {
    // ── Auth flow ──────────────────────────────────────────────────────────
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object SignIn : Screen("sign_in")
    data object SignUp : Screen("sign_up")
    data object ProfileSuccess : Screen("profile_success")
    data object ForgotPassword : Screen("forgot_password")
    data object RecoveryEmailSent : Screen("recovery_email_sent")
    data object ResetPassword : Screen("reset_password")
    data object RecoverySuccess : Screen("recovery_success")

    // ── Main tabs ──────────────────────────────────────────────────────────
    data object Home : Screen("home")
    data object Profile : Screen("profile")

    // ── Profile sub-screens ────────────────────────────────────────────────
    data object EditProfile : Screen("edit_profile")
    data object Notification : Screen("notification")
    data object AudioVideo : Screen("audio_video")
    data object Playback : Screen("playback")
    data object DataSaver : Screen("data_saver")
    data object Security : Screen("security")
    data object Language : Screen("language")

    // ── Subscription flow ──────────────────────────────────────────────────
    data object PickPlan : Screen("pick_plan")
    data object PaymentMethod : Screen("payment_method")
    data object AddCard : Screen("add_card")
    data object ReviewSummary : Screen("review_summary")
    data object Congratulations : Screen("congratulations")
}

@Composable
fun BeatlyNavGraph() {
    val nav = rememberNavController()

    // Helper lambdas
    fun popBack() = nav.popBackStack()
    fun goTo(s: Screen, popInclusive: Screen? = null) {
        nav.navigate(s.route) {
            popInclusive?.let { popUpTo(it.route) { inclusive = true } }
        }
    }

    NavHost(navController = nav, startDestination = Screen.Splash.route) {

        // ── Splash ─────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = { goTo(Screen.Onboarding, Screen.Splash) })
        }

        // ── Onboarding ─────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueFinished = { goTo(Screen.SignIn, Screen.Onboarding) },
                onRegisterClicked = { nav.navigate(Screen.SignUp.route) }
            )
        }

        // ── Sign In ────────────────────────────────────────────────────────
        composable(Screen.SignIn.route) {
            SignInScreen(
                onBackClicked = { popBack() },
                onSignInSuccess = { goTo(Screen.Home, Screen.SignIn) },
                onForgotPassword = { nav.navigate(Screen.ForgotPassword.route) },
                onRegisterClicked = { nav.navigate(Screen.SignUp.route) },
                onAppleSignIn = { },
                onFacebookSignIn = { }
            )
        }

        // ── Sign Up ────────────────────────────────────────────────────────
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClicked = { popBack() },
                onSignUpSuccess = { goTo(Screen.ProfileSuccess, Screen.SignUp) },
                onTermsClicked = { },
                onPrivacyClicked = { }
            )
        }

        // ── Profile Setup Success ──────────────────────────────────────────
        composable(Screen.ProfileSuccess.route) {
            ProfileSuccessScreen(
                onContinue = { goTo(Screen.Home, Screen.ProfileSuccess) },
                onCallSupport = { }
            )
        }

        // ── Forgot Password ────────────────────────────────────────────────
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClicked = { popBack() },
                onContinue = { nav.navigate(Screen.RecoveryEmailSent.route) },
                onCallSupport = { }
            )
        }

        composable(Screen.RecoveryEmailSent.route) {
            RecoveryEmailSentScreen(
                onContinue = { nav.navigate(Screen.ResetPassword.route) },
                onCallSupport = { }
            )
        }

        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClicked = { popBack() },
                onContinue = { goTo(Screen.RecoverySuccess, Screen.ForgotPassword) },
                onCallSupport = { }
            )
        }

        composable(Screen.RecoverySuccess.route) {
            RecoverySuccessScreen(
                onContinue = { goTo(Screen.SignIn, Screen.RecoverySuccess) },
                onCallSupport = { }
            )
        }

        // ── Home (tab root) ────────────────────────────────────────────────
        composable(Screen.Home.route) {
            // Placeholder — replace with actual HomeScreen
            HomeScreen(
                onSearchClick = { },
                onSeeAllTrendingClick = { },
                onSeeAllArtistsClick = { },
                onSeeAllRecentClick = { },
                onArtistClick = { },
                onNavigateTab = { tab ->
                    when (tab) {
                        BeatlyTab.PROFILE -> goTo(Screen.Profile)
                        else -> {}
                    }
                }, onSongClick = {}
            )
        }

        // ── Profile (tab) ──────────────────────────────────────────────────
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClicked = { popBack() },
                onGetPremium = { nav.navigate(Screen.PickPlan.route) },
                onShareProfile = { },   // show bottom sheet inside ProfileScreen
                onEditProfile = { nav.navigate(Screen.EditProfile.route) },
                onSettingClicked = { id ->
                    when (id) {
                        "profile" -> nav.navigate(Screen.EditProfile.route)
                        "notification" -> nav.navigate(Screen.Notification.route)
                        "audio_video" -> nav.navigate(Screen.AudioVideo.route)
                        "playback" -> nav.navigate(Screen.Playback.route)
                        "downloads" -> nav.navigate(Screen.DataSaver.route)
                        "security" -> nav.navigate(Screen.Security.route)
                        "language" -> nav.navigate(Screen.Language.route)
                        else -> {}
                    }
                },
                onNavigateTab = { tab ->
                    when (tab) {
                        BeatlyTab.HOME -> goTo(Screen.Home)
                        else -> {}
                    }
                }
            )
        }

        // ── Settings sub-screens ───────────────────────────────────────────
        composable(Screen.EditProfile.route) {
            EditProfileScreen(onBackClicked = { popBack() }, onUpdated = { popBack() })
        }
        composable(Screen.Notification.route) {
            NotificationScreen(onBackClicked = { popBack() }, onUpdated = { popBack() })
        }
        composable(Screen.AudioVideo.route) {
            AudioVideoScreen(onBackClicked = { popBack() }, onUpdated = { popBack() })
        }
        composable(Screen.Playback.route) {
            PlaybackScreen(onBackClicked = { popBack() }, onUpdated = { popBack() })
        }
        composable(Screen.DataSaver.route) {
            DataSaverScreen(onBackClicked = { popBack() }, onUpdated = { popBack() })
        }
        composable(Screen.Security.route) {
            SecurityScreen(onBackClicked = { popBack() }, onChangePin = { }, onChangePassword = { })
        }
        composable(Screen.Language.route) {
            LanguageScreen(onBackClicked = { popBack() }, onChanged = { popBack() })
        }

        // ── Subscription flow ──────────────────────────────────────────────
        composable(Screen.PickPlan.route) {
            PickPlanScreen(
                onBackClicked = { popBack() },
                onContinue = { nav.navigate(Screen.PaymentMethod.route) })
        }
        composable(Screen.PaymentMethod.route) {
            PaymentMethodScreen(
                onBackClicked = { popBack() },
                onAddCard = { nav.navigate(Screen.AddCard.route) },
                onContinue = { nav.navigate(Screen.ReviewSummary.route) }
            )
        }
        composable(Screen.AddCard.route) {
            AddCardScreen(onBackClicked = { popBack() }, onAddCard = { popBack() })
        }
        composable(Screen.ReviewSummary.route) {
            ReviewSummaryScreen(
                onBackClicked = { popBack() },
                onConfirm = { goTo(Screen.Congratulations, Screen.PickPlan) },
                onChangeMethod = { popBack() }
            )
        }
        composable(Screen.Congratulations.route) {
            CongratulationsScreen(onBackToHome = { goTo(Screen.Home, Screen.Congratulations) })
        }
    }
}