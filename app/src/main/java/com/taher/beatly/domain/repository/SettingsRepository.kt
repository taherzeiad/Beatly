package com.taher.beatly.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    val language: Flow<String>
    
    // Audio & Video
    val wifiAudio: Flow<String>
    val cellularAudio: Flow<String>
    val autoAdjustQuality: Flow<Boolean>
    val downloadQuality: Flow<String>

    // Playback
    val gapless: Flow<Boolean>
    val automix: Flow<Boolean>
    val explicit: Flow<Boolean>
    val normalize: Flow<Boolean>

    // Security
    val rememberMe: Flow<Boolean>
    val faceId: Flow<Boolean>
    val biometricId: Flow<Boolean>

    // Data Saver
    val audioQualitySaver: Flow<Boolean>
    val downloadAudioOnly: Flow<Boolean>
    val streamAudioOnly: Flow<Boolean>

    // Notifications
    val notifRecommended: Flow<Boolean>
    val notifNewMusic: Flow<Boolean>
    val notifPlaylist: Flow<Boolean>
    val notifConcert: Flow<Boolean>
    val notifArtist: Flow<Boolean>
    val notifNews: Flow<Boolean>
    val notifEvents: Flow<Boolean>

    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setLanguage(lang: String)
    
    suspend fun setWifiAudio(v: String)
    suspend fun setCellularAudio(v: String)
    suspend fun setAutoAdjustQuality(v: Boolean)
    suspend fun setDownloadQuality(v: String)

    suspend fun setGapless(v: Boolean)
    suspend fun setAutomix(v: Boolean)
    suspend fun setExplicit(v: Boolean)
    suspend fun setNormalize(v: Boolean)

    suspend fun setRememberMe(v: Boolean)
    suspend fun setFaceId(v: Boolean)
    suspend fun setBiometricId(v: Boolean)

    suspend fun setAudioQualitySaver(v: Boolean)
    suspend fun setDownloadAudioOnly(v: Boolean)
    suspend fun setStreamAudioOnly(v: Boolean)

    suspend fun setNotifRecommended(v: Boolean)
    suspend fun setNotifNewMusic(v: Boolean)
    suspend fun setNotifPlaylist(v: Boolean)
    suspend fun setNotifConcert(v: Boolean)
    suspend fun setNotifArtist(v: Boolean)
    suspend fun setNotifNews(v: Boolean)
    suspend fun setNotifEvents(v: Boolean)

    suspend fun clearAll()
}
