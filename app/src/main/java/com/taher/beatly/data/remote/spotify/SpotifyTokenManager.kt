package com.taher.beatly.data.remote.spotify

import android.util.Base64
import android.util.Log
import com.taher.beatly.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyTokenManager @Inject constructor(
    private val tokenService: SpotifyTokenService
) {
    private val clientId = BuildConfig.SPOTIFY_CLIENT_ID
    private val clientSecret = BuildConfig.SPOTIFY_CLIENT_SECRET

    private var accessToken: String = ""
    private var expiresAtMs: Long = 0L

    suspend fun getValidToken(): String {
        if (accessToken.isNotEmpty() && System.currentTimeMillis() < expiresAtMs) {
            Log.d("SpotifyToken", "Using cached token")
            return "Bearer $accessToken"
        }
        return refreshToken()
    }

    private suspend fun refreshToken(): String {
        Log.d("SpotifyToken", "Refreshing token...")
        if (clientId.isNullOrEmpty() || clientSecret.isNullOrEmpty()) {
            Log.e("SpotifyToken", "Spotify Client ID or Secret is missing in BuildConfig")
            return ""
        }
        return try {
            val credentials = Base64.encodeToString(
                "$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP
            )
            val response = tokenService.getToken(basicAuth = "Basic $credentials")
            accessToken = response.access_token
            expiresAtMs = System.currentTimeMillis() + (response.expires_in * 1000L) - 60_000L
            Log.d("SpotifyToken", "Token refreshed successfully. Expires in ${response.expires_in}s")
            "Bearer $accessToken"
        } catch (e: Exception) {
            Log.e("SpotifyToken", "Failed to refresh token: ${e.message}", e)
            ""
        }
    }
}
