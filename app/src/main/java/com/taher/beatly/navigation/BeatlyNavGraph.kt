package com.taher.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taher.beatly.ui.artist.ArtistDetailScreen
import com.taher.beatly.ui.auth.ProfileSuccessScreen
import com.taher.beatly.ui.auth.RecoverySuccessScreen
import com.taher.beatly.ui.auth.forgotpassword.ForgotPasswordScreen
import com.taher.beatly.ui.auth.recoveryemail.RecoveryEmailSentScreen
import com.taher.beatly.ui.auth.resetpassword.ResetPasswordScreen
import com.taher.beatly.ui.auth.signIn.SignInScreen
import com.taher.beatly.ui.auth.signup.SignUpScreen
import com.taher.beatly.ui.components.BeatlyTab
import com.taher.beatly.ui.genre.AllGenreScreen
import com.taher.beatly.ui.home.HomeScreen
import com.taher.beatly.ui.library.LikedSongsScreen
import com.taher.beatly.ui.library.MyLibraryScreen
import com.taher.beatly.ui.onboarding.OnboardingScreen
import com.taher.beatly.ui.player.PlayMusicScreen
import com.taher.beatly.ui.search.SearchArtistsScreen
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
    data object Home                : Screen("home")
    data object Search              : Screen("search")
    data object Library             : Screen("library")
    data object AllGenre            : Screen("genres")
    data object LikedSongs          : Screen("liked_songs")
    data object Player              : Screen("player")
    data object ArtistDetail : Screen("artist/{artistId}") {
        fun createRoute(artistId: String) = "artist/$artistId"
    }
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
                onAppleSignIn     = {  },
                onFacebookSignIn  = {  }
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
                onTermsClicked   = {  },
                onPrivacyClicked = {  }
            )
        }

        // ── Profile Setup Success ──────────────────────────────────────────
        composable(Screen.ProfileSuccess.route) {
            ProfileSuccessScreen(
                onContinue    = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onCallSupport = { /* Open dialer or support */ }
            )
        }

        // ── Forgot Password ────────────────────────────────────────────────
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClicked = { navController.popBackStack() },
                onContinue    = { navController.navigate(Screen.RecoveryEmailSent.route) },
                onCallSupport = {  }
            )
        }

        // ── Recovery Email Sent ────────────────────────────────────────────
        composable(Screen.RecoveryEmailSent.route) {
            RecoveryEmailSentScreen(
                onContinue    = { navController.navigate(Screen.ResetPassword.route) },
                onCallSupport = { }
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
                onCallSupport = {  }
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
                onCallSupport = { /* Open support */ }
            )
        }

        // ── Home ───────────────────────────────────────────────────────────
        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onSeeAllTrendingClick = { /* Navigate to trending list */ },
                onSeeAllArtistsClick = { navController.navigate(Screen.Search.route) },
                onSeeAllRecentClick = { /* Navigate to recent list */ },
                onArtistClick = { artistId ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                },
                onNavigateTab = { tab ->
                    when (tab) {
                        BeatlyTab.HOME -> {}
                        BeatlyTab.EXPLORE -> navController.navigate(Screen.Search.route)
                        BeatlyTab.LIBRARY -> navController.navigate(Screen.Library.route)
                        BeatlyTab.PROFILE -> { /* Navigate to profile */ }
                    }
                }
            )
        }

        // ── Search / Explore ───────────────────────────────────────────────
        composable(Screen.Search.route) {
            SearchArtistsScreen(
                onBackClick = { navController.popBackStack() },
                onArtistClick = { artistId ->
                    navController.navigate(Screen.ArtistDetail.createRoute(artistId))
                }
            )
        }

        // ── Library ────────────────────────────────────────────────────────
        composable(Screen.Library.route) {
            MyLibraryScreen(
                onBackClick = { navController.popBackStack() },
                onLibraryItemClick = { item ->
                    if (item.id == "l1") { // Hardcoded ID for Liked Songs in FakeRepo
                        navController.navigate(Screen.LikedSongs.route)
                    }
                }
            )
        }

        // ── Liked Songs ────────────────────────────────────────────────────
        composable(Screen.LikedSongs.route) {
            LikedSongsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }

        // ── Artist Detail ──────────────────────────────────────────────────
        composable(
            route = Screen.ArtistDetail.route,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) {
            ArtistDetailScreen(
                onBackClick = { navController.popBackStack() },
                onSeeAllSongsClick = { /* Navigate to all songs */ }
            )
        }

        // ── Genres ─────────────────────────────────────────────────────────
        composable(Screen.AllGenre.route) {
            AllGenreScreen(
                onBackClick = { navController.popBackStack() },
                onSearchClick = { navController.navigate(Screen.Search.route) },
                onGenreClick = { /* Navigate to genre details */ }
            )
        }

        // ── Player ─────────────────────────────────────────────────────────
        composable(Screen.Player.route) {
            PlayMusicScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
