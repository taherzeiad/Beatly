package com.taher.beatly.data.remote.spotify

import android.util.Base64
import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SpotifyTokenManager @Inject constructor(
    private val tokenService: SpotifyTokenService
) {
    // ⚠️ Store these in local.properties → BuildConfig, never hardcode in prod
    private val clientId = "YOUR_SPOTIFY_CLIENT_ID"
    private val clientSecret = "YOUR_SPOTIFY_CLIENT_SECRET"

    private var accessToken: String = ""
    private var expiresAtMs: Long = 0L

    suspend fun getValidToken(): String {
        if (accessToken.isNotEmpty() && System.currentTimeMillis() < expiresAtMs) {
            return "Bearer $accessToken"
        }
        return refreshToken()
    }

    private suspend fun refreshToken(): String {
        return try {
            val credentials = Base64.encodeToString(
                "$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP
            )
            val response = tokenService.getToken(basicAuth = "Basic $credentials")
            accessToken = response.access_token
            expiresAtMs = System.currentTimeMillis() + (response.expires_in * 1000L) - 60_000L
            "Bearer $accessToken"
        } catch (e: Exception) {
            Log.e("SpotifyToken", "Failed to refresh token", e)
            ""
        }
    }
}