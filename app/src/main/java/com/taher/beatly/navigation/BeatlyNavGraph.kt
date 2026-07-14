package com.taher.beatly.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.taher.beatly.ui.profile.ProfileScreen
import com.taher.beatly.ui.artist.ArtistDetailScreen
import com.taher.beatly.ui.artist.FollowedArtistsScreen
import com.taher.beatly.ui.auth.ProfileSuccessScreen
import com.taher.beatly.ui.auth.RecoverySuccessScreen
import com.taher.beatly.ui.auth.forgotpassword.ForgotPasswordScreen
import com.taher.beatly.ui.auth.recoveryemail.RecoveryEmailSentScreen
import com.taher.beatly.ui.auth.resetpassword.ResetPasswordScreen
import com.taher.beatly.ui.auth.signIn.SignInScreen
import com.taher.beatly.ui.auth.signup.SignUpScreen
import com.taher.beatly.ui.components.BeatlyTab
import com.taher.beatly.ui.components.SongListScreen
import com.taher.beatly.ui.components.SongListSource
import com.taher.beatly.ui.genre.AllGenreScreen
import com.taher.beatly.ui.home.HomeScreen
import com.taher.beatly.ui.library.LikedSongsScreen
import com.taher.beatly.ui.library.MyLibraryScreen
import com.taher.beatly.ui.onboarding.OnboardingScreen
import com.taher.beatly.ui.player.PlayMusicScreen
import com.taher.beatly.ui.search.SearchArtistsScreen
import com.taher.beatly.ui.settings.AudioVideoScreen
import com.taher.beatly.ui.settings.DataSaverScreen
import com.taher.beatly.ui.settings.EditProfileScreen
import com.taher.beatly.ui.settings.LanguageScreen
import com.taher.beatly.ui.settings.NotificationScreen
import com.taher.beatly.ui.settings.PlaybackScreen
import com.taher.beatly.ui.settings.SecurityScreen
import com.taher.beatly.ui.splash.SplashScreen
import com.taher.beatly.ui.subscription.AddCardScreen
import com.taher.beatly.ui.subscription.CongratulationsScreen
import com.taher.beatly.ui.subscription.PaymentMethodScreen
import com.taher.beatly.ui.subscription.PickPlanScreen
import com.taher.beatly.ui.subscription.ReviewSummaryScreen

sealed class Screen(val route: String) {

    // --- Auth flow ---
    data object Splash            : Screen("splash")
    data object Onboarding        : Screen("onboarding")
    data object SignIn            : Screen("sign_in")
    data object SignUp            : Screen("sign_up")
    data object ProfileSuccess    : Screen("profile_success")
    data object ForgotPassword    : Screen("forgot_password")
    data object RecoveryEmailSent : Screen("recovery_email_sent")
    data object ResetPassword     : Screen("reset_password")
    data object RecoverySuccess   : Screen("recovery_success")

    // --- Main tabs ---
    data object Home    : Screen("home")
    data object Search  : Screen("search?query={query}") {
        fun createRoute(query: String? = null) = if (query != null) "search?query=$query" else "search"
    }
    data object Library : Screen("library")
    data object Profile : Screen("profile")

    // --- Home / library sub-screens ---
    data object AllGenre   : Screen("genres")
    data object LikedSongs : Screen("liked_songs")
    data object FollowedArtists : Screen("followed_artists")
    data object Player     : Screen("player")
    data object ArtistDetail : Screen("artist/{artistId}") {
        fun createRoute(artistId: String) = "artist/$artistId"
    }
    data object SongList : Screen("song_list/{source}/{id}?title={title}") {
        fun createRoute(source: SongListSource, id: String, title: String? = null) =
            "song_list/${source.name}/$id" + if (title != null) "?title=$title" else ""
    }

    // --- Profile sub-screens ---
    data object EditProfile : Screen("edit_profile")
    data object Notification : Screen("notification")
    data object AudioVideo   : Screen("audio_video")
    data object Playback     : Screen("playback")
    data object DataSaver    : Screen("data_saver")
    data object Security     : Screen("security")
    data object Language     : Screen("language")

    // --- Subscription flow ---
    data object PickPlan        : Screen("pick_plan")
    data object PaymentMethod   : Screen("payment_method")
    data object AddCard         : Screen("add_card")
    data object ReviewSummary   : Screen("review_summary")
    data object Congratulations : Screen("congratulations")
}

@Composable
fun BeatlyNavGraph() {
    val nav = rememberNavController()

    fun popBack() = nav.popBackStack()
    fun goTo(s: Screen, popInclusive: Screen? = null) {
        nav.navigate(s.route) {
            popInclusive?.let { popUpTo(it.route) { inclusive = true } }
        }
    }

    NavHost(navController = nav, startDestination = Screen.Splash.route) {

        // ===================== Splash =====================
        composable(Screen.Splash.route) {
            SplashScreen(onSplashFinished = { goTo(Screen.Onboarding, Screen.Splash) })
        }

        // ===================== Onboarding =====================
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinueFinished = { goTo(Screen.SignIn, Screen.Onboarding) },
                onRegisterClicked  = { nav.navigate(Screen.SignUp.route) }
            )
        }

        // ===================== Sign In =====================
        composable(Screen.SignIn.route) {
            SignInScreen(
                onBackClicked     = { popBack() },
                onSignInSuccess   = { goTo(Screen.ProfileSuccess, Screen.SignIn) },
                onForgotPassword  = { nav.navigate(Screen.ForgotPassword.route) },
                onRegisterClicked = { nav.navigate(Screen.SignUp.route) },
                onAppleSignIn     = { },
                onFacebookSignIn  = { }
            )
        }

        // ===================== Sign Up =====================
        composable(Screen.SignUp.route) {
            SignUpScreen(
                onBackClicked    = { popBack() },
                onSignUpSuccess  = { goTo(Screen.ProfileSuccess, Screen.SignUp) },
                onTermsClicked   = { },
                onPrivacyClicked = { }
            )
        }

        // ===================== Profile Setup Success =====================
        composable(Screen.ProfileSuccess.route) {
            ProfileSuccessScreen(
                onContinue    = { goTo(Screen.Home, Screen.ProfileSuccess) },
                onCallSupport = { }
            )
        }

        // ===================== Forgot Password =====================
        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBackClicked = { popBack() },
                onContinue    = { nav.navigate(Screen.RecoveryEmailSent.route) },
                onCallSupport = { }
            )
        }

        // ===================== Recovery Email Sent =====================
        composable(Screen.RecoveryEmailSent.route) {
            RecoveryEmailSentScreen(
                onContinue    = { nav.navigate(Screen.ResetPassword.route) },
                onCallSupport = { }
            )
        }

        // ===================== Reset Password =====================
        composable(Screen.ResetPassword.route) {
            ResetPasswordScreen(
                onBackClicked = { popBack() },
                onContinue    = { goTo(Screen.RecoverySuccess, Screen.ForgotPassword) },
                onCallSupport = { }
            )
        }

        // ===================== Recovery Success =====================
        composable(Screen.RecoverySuccess.route) {
            RecoverySuccessScreen(
                onContinue    = { goTo(Screen.SignIn, Screen.RecoverySuccess) },
                onCallSupport = { }
            )
        }

        // ===================== Home =====================
        composable(Screen.Home.route) {
            HomeScreen(
                onSearchClick          = { nav.navigate(Screen.Search.route) },
                onSeeAllTrendingClick  = { nav.navigate(Screen.AllGenre.route) },
                onSeeAllArtistsClick   = { nav.navigate(Screen.Search.route) },
                onSeeAllRecentClick    = {
                    nav.navigate(Screen.SongList.createRoute(SongListSource.RECENT, "recent", "Recently Played"))
                },
                onArtistClick = { artistId ->
                    nav.navigate(Screen.ArtistDetail.createRoute(artistId))
                },
                onSongClick = { _ ->
                    nav.navigate(Screen.Player.route)
                },
                onNavigateTab = { tab ->
                    when (tab) {
                        BeatlyTab.HOME    -> { }
                        BeatlyTab.EXPLORE -> nav.navigate(Screen.Search.route)
                        BeatlyTab.LIBRARY -> nav.navigate(Screen.Library.route)
                        BeatlyTab.PROFILE -> nav.navigate(Screen.Profile.route)
                    }
                }
            )
        }

        // ===================== Search / Explore =====================
        composable(
            route = Screen.Search.route,
            arguments = listOf(navArgument("query") { nullable = true; defaultValue = null })
        ) { backStackEntry ->
            val initialQuery = backStackEntry.arguments?.getString("query")
            SearchArtistsScreen(
                onBackClick = { popBack() },
                onArtistClick = { artistId ->
                    nav.navigate(Screen.ArtistDetail.createRoute(artistId))
                },
                onSongClick = { _ ->
                    nav.navigate(Screen.Player.route)
                },
                onAlbumClick = { album ->
                    nav.navigate(Screen.SongList.createRoute(SongListSource.ALBUM, album.id, album.name))
                },
                onPlaylistClick = { playlist ->
                    nav.navigate(Screen.SongList.createRoute(SongListSource.PLAYLIST, playlist.id, playlist.name))
                },
                initialQuery = initialQuery
            )
        }

        // ===================== Library =====================
        composable(Screen.Library.route) {
            MyLibraryScreen(
                onBackClick = { popBack() },
                onLibraryItemClick = { item ->
                    when (item.id) {
                        "liked_songs" -> nav.navigate(Screen.LikedSongs.route)
                        "followed_artists" -> nav.navigate(Screen.FollowedArtists.route)
                        else -> {
                            nav.navigate(Screen.SongList.createRoute(SongListSource.PLAYLIST, item.id, item.name))
                        }
                    }
                }
            )
        }

        // ===================== Liked Songs =====================
        composable(Screen.LikedSongs.route) {
            LikedSongsScreen(
                onBackClick = { popBack() },
                onSongClick = { _ ->
                    nav.navigate(Screen.Player.route)
                }
            )
        }

        // ===================== Followed Artists =====================
        composable(Screen.FollowedArtists.route) {
            FollowedArtistsScreen(
                onBackClick = { popBack() },
                onArtistClick = { artistId ->
                    nav.navigate(Screen.ArtistDetail.createRoute(artistId))
                }
            )
        }

        // ===================== Artist Detail =====================
        composable(
            route     = Screen.ArtistDetail.route,
            arguments = listOf(navArgument("artistId") { type = NavType.StringType })
        ) {
            ArtistDetailScreen(
                onBackClick         = { popBack() },
                onSeeAllSongsClick  = { /* Navigate to all songs */ },
                onSongClick = { _ ->
                    nav.navigate(Screen.Player.route)
                }
            )
        }

        // ===================== Genres =====================
        composable(Screen.AllGenre.route) {
            AllGenreScreen(
                onBackClick   = { popBack() },
                onSearchClick = { nav.navigate(Screen.Search.createRoute()) },
                onGenreClick  = { genreName ->
                    nav.navigate(Screen.SongList.createRoute(SongListSource.GENRE, genreName, genreName))
                }
            )
        }

        // ===================== Song List (Album/Playlist/Genre) =====================
        composable(
            route = Screen.SongList.route,
            arguments = listOf(
                navArgument("source") { type = NavType.StringType },
                navArgument("id") { type = NavType.StringType },
                navArgument("title") { nullable = true; defaultValue = null }
            )
        ) {
            SongListScreen(
                onBackClick = { popBack() },
                onSongClick = { _ ->
                    nav.navigate(Screen.Player.route)
                }
            )
        }

        // ===================== Player =====================
        composable(Screen.Player.route) {
            PlayMusicScreen(onBackClick = { popBack() })
        }

        // ===================== Profile (tab) =====================
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBackClicked   = { popBack() },
                onGetPremium    = { nav.navigate(Screen.PickPlan.route) },
                onShareProfile  = { },   // show bottom sheet inside ProfileScreen
                onEditProfile   = { nav.navigate(Screen.EditProfile.route) },
                onSettingClicked = { id ->
                    when (id) {
                        "profile"      -> nav.navigate(Screen.EditProfile.route)
                        "notification" -> nav.navigate(Screen.Notification.route)
                        "audio_video"  -> nav.navigate(Screen.AudioVideo.route)
                        "playback"     -> nav.navigate(Screen.Playback.route)
                        "downloads"    -> nav.navigate(Screen.DataSaver.route)
                        "security"     -> nav.navigate(Screen.Security.route)
                        "language"     -> nav.navigate(Screen.Language.route)
                        else -> { }
                    }
                },
                onNavigateTab = { tab ->
                    when (tab) {
                        BeatlyTab.HOME    -> goTo(Screen.Home)
                        BeatlyTab.EXPLORE -> nav.navigate(Screen.Search.route)
                        BeatlyTab.LIBRARY -> nav.navigate(Screen.Library.route)
                        BeatlyTab.PROFILE -> { }
                    }
                }
            )
        }

        // ===================== Profile sub-screens =====================
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

        // ===================== Subscription Flow =====================
        composable(Screen.PickPlan.route) {
            PickPlanScreen(
                onBackClicked = { popBack() },
                onContinue    = { nav.navigate(Screen.PaymentMethod.route) }
            )
        }

        composable(Screen.PaymentMethod.route) {
            PaymentMethodScreen(
                onBackClicked = { popBack() },
                onAddCard     = { nav.navigate(Screen.AddCard.route) },
                onContinue    = { nav.navigate(Screen.ReviewSummary.route) }
            )
        }

        composable(Screen.AddCard.route) {
            AddCardScreen(
                onBackClicked = { popBack() },
                onAddCard     = { popBack() }
            )
        }

        composable(Screen.ReviewSummary.route) {
            ReviewSummaryScreen(
                onBackClicked  = { popBack() },
                onConfirm      = { goTo(Screen.Congratulations, Screen.PickPlan) },
                onChangeMethod = { popBack() }
            )
        }

        composable(Screen.Congratulations.route) {
            CongratulationsScreen(
                onBackToHome = { goTo(Screen.Home, Screen.Congratulations) }
            )
        }
    }
}