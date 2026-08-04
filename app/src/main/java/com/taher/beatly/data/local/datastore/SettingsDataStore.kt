package com.taher.beatly.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "beatly_settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val KEY_DARK_MODE = booleanPreferencesKey("dark_mode")
        val KEY_LANGUAGE  = stringPreferencesKey("language")
        val KEY_USER_ID   = stringPreferencesKey("user_id")

        // Audio & Video
        val KEY_WIFI_AUDIO = stringPreferencesKey("wifi_audio")
        val KEY_CELLULAR_AUDIO = stringPreferencesKey("cellular_audio")
        val KEY_AUTO_ADJUST_QUALITY = booleanPreferencesKey("auto_adjust_quality")
        val KEY_DOWNLOAD_QUALITY = stringPreferencesKey("download_quality")

        // Playback
        val KEY_GAPLESS = booleanPreferencesKey("gapless")
        val KEY_AUTOMIX = booleanPreferencesKey("automix")
        val KEY_EXPLICIT = booleanPreferencesKey("explicit")
        val KEY_NORMALIZE = booleanPreferencesKey("normalize")

        // Security
        val KEY_REMEMBER_ME = booleanPreferencesKey("remember_me")
        val KEY_FACE_ID = booleanPreferencesKey("face_id")
        val KEY_BIOMETRIC_ID = booleanPreferencesKey("biometric_id")

        // Data Saver
        val KEY_AUDIO_QUALITY_SAVER = booleanPreferencesKey("audio_quality_saver")
        val KEY_DOWNLOAD_AUDIO_ONLY = booleanPreferencesKey("download_audio_only")
        val KEY_STREAM_AUDIO_ONLY = booleanPreferencesKey("stream_audio_only")

        // Notifications
        val KEY_NOTIF_RECOMMENDED = booleanPreferencesKey("notif_recommended")
        val KEY_NOTIF_NEW_MUSIC = booleanPreferencesKey("notif_new_music")
        val KEY_NOTIF_PLAYLIST = booleanPreferencesKey("notif_playlist")
        val KEY_NOTIF_CONCERT = booleanPreferencesKey("notif_concert")
        val KEY_NOTIF_ARTIST = booleanPreferencesKey("notif_artist")
        val KEY_NOTIF_NEWS = booleanPreferencesKey("notif_news")
        val KEY_NOTIF_EVENTS = booleanPreferencesKey("notif_events")
    }

    // ── Dark mode ──────────────────────────────────────────────────────────
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map   { it[KEY_DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

    // ── Audio & Video ──────────────────────────────────────────────────────
    val wifiAudio: Flow<String> = context.dataStore.data.map { it[KEY_WIFI_AUDIO] ?: "High" }
    val cellularAudio: Flow<String> = context.dataStore.data.map { it[KEY_CELLULAR_AUDIO] ?: "Automatic" }
    val autoAdjustQuality: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTO_ADJUST_QUALITY] ?: true }
    val downloadQuality: Flow<String> = context.dataStore.data.map { it[KEY_DOWNLOAD_QUALITY] ?: "Normal" }

    suspend fun setWifiAudio(v: String) { context.dataStore.edit { it[KEY_WIFI_AUDIO] = v } }
    suspend fun setCellularAudio(v: String) { context.dataStore.edit { it[KEY_CELLULAR_AUDIO] = v } }
    suspend fun setAutoAdjustQuality(v: Boolean) { context.dataStore.edit { it[KEY_AUTO_ADJUST_QUALITY] = v } }
    suspend fun setDownloadQuality(v: String) { context.dataStore.edit { it[KEY_DOWNLOAD_QUALITY] = v } }

    // ── Playback ───────────────────────────────────────────────────────────
    val gapless: Flow<Boolean> = context.dataStore.data.map { it[KEY_GAPLESS] ?: true }
    val automix: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUTOMIX] ?: true }
    val explicit: Flow<Boolean> = context.dataStore.data.map { it[KEY_EXPLICIT] ?: false }
    val normalize: Flow<Boolean> = context.dataStore.data.map { it[KEY_NORMALIZE] ?: true }

    suspend fun setGapless(v: Boolean) { context.dataStore.edit { it[KEY_GAPLESS] = v } }
    suspend fun setAutomix(v: Boolean) { context.dataStore.edit { it[KEY_AUTOMIX] = v } }
    suspend fun setExplicit(v: Boolean) { context.dataStore.edit { it[KEY_EXPLICIT] = v } }
    suspend fun setNormalize(v: Boolean) { context.dataStore.edit { it[KEY_NORMALIZE] = v } }

    // ── Security ───────────────────────────────────────────────────────────
    val rememberMe: Flow<Boolean> = context.dataStore.data.map { it[KEY_REMEMBER_ME] ?: true }
    val faceId: Flow<Boolean> = context.dataStore.data.map { it[KEY_FACE_ID] ?: false }
    val biometricId: Flow<Boolean> = context.dataStore.data.map { it[KEY_BIOMETRIC_ID] ?: true }

    suspend fun setRememberMe(v: Boolean) { context.dataStore.edit { it[KEY_REMEMBER_ME] = v } }
    suspend fun setFaceId(v: Boolean) { context.dataStore.edit { it[KEY_FACE_ID] = v } }
    suspend fun setBiometricId(v: Boolean) { context.dataStore.edit { it[KEY_BIOMETRIC_ID] = v } }

    // ── Data Saver ─────────────────────────────────────────────────────────
    val audioQualitySaver: Flow<Boolean> = context.dataStore.data.map { it[KEY_AUDIO_QUALITY_SAVER] ?: true }
    val downloadAudioOnly: Flow<Boolean> = context.dataStore.data.map { it[KEY_DOWNLOAD_AUDIO_ONLY] ?: true }
    val streamAudioOnly: Flow<Boolean> = context.dataStore.data.map { it[KEY_STREAM_AUDIO_ONLY] ?: true }

    suspend fun setAudioQualitySaver(v: Boolean) { context.dataStore.edit { it[KEY_AUDIO_QUALITY_SAVER] = v } }
    suspend fun setDownloadAudioOnly(v: Boolean) { context.dataStore.edit { it[KEY_DOWNLOAD_AUDIO_ONLY] = v } }
    suspend fun setStreamAudioOnly(v: Boolean) { context.dataStore.edit { it[KEY_STREAM_AUDIO_ONLY] = v } }

    // ── Notifications ──────────────────────────────────────────────────────
    val notifRecommended: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_RECOMMENDED] ?: true }
    val notifNewMusic: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_NEW_MUSIC] ?: true }
    val notifPlaylist: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_PLAYLIST] ?: true }
    val notifConcert: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_CONCERT] ?: true }
    val notifArtist: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_ARTIST] ?: true }
    val notifNews: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_NEWS] ?: true }
    val notifEvents: Flow<Boolean> = context.dataStore.data.map { it[KEY_NOTIF_EVENTS] ?: true }

    suspend fun setNotifRecommended(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_RECOMMENDED] = v } }
    suspend fun setNotifNewMusic(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_NEW_MUSIC] = v } }
    suspend fun setNotifPlaylist(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_PLAYLIST] = v } }
    suspend fun setNotifConcert(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_CONCERT] = v } }
    suspend fun setNotifArtist(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_ARTIST] = v } }
    suspend fun setNotifNews(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_NEWS] = v } }
    suspend fun setNotifEvents(v: Boolean) { context.dataStore.edit { it[KEY_NOTIF_EVENTS] = v } }

    // ── Language ───────────────────────────────────────────────────────────
    val language: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map   { it[KEY_LANGUAGE] ?: "English (US)" }

    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { it[KEY_LANGUAGE] = lang }
    }

    // ── Session ────────────────────────────────────────────────────────────
    val userId: Flow<String> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map   { it[KEY_USER_ID] ?: "" }

    suspend fun setUserId(id: String) {
        context.dataStore.edit { it[KEY_USER_ID] = id }
    }

    // ── Clear all on logout ────────────────────────────────────────────────
    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}