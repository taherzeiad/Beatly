package com.taher.beatly.data.repository

import com.taher.beatly.data.local.datastore.SettingsDataStore
import com.taher.beatly.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: SettingsDataStore
) : SettingsRepository {
    override val isDarkMode: Flow<Boolean> = dataStore.isDarkMode
    override val language: Flow<String> = dataStore.language
    
    override val wifiAudio: Flow<String> = dataStore.wifiAudio
    override val cellularAudio: Flow<String> = dataStore.cellularAudio
    override val autoAdjustQuality: Flow<Boolean> = dataStore.autoAdjustQuality
    override val downloadQuality: Flow<String> = dataStore.downloadQuality

    override val gapless: Flow<Boolean> = dataStore.gapless
    override val automix: Flow<Boolean> = dataStore.automix
    override val explicit: Flow<Boolean> = dataStore.explicit
    override val normalize: Flow<Boolean> = dataStore.normalize

    override val rememberMe: Flow<Boolean> = dataStore.rememberMe
    override val faceId: Flow<Boolean> = dataStore.faceId
    override val biometricId: Flow<Boolean> = dataStore.biometricId

    override val audioQualitySaver: Flow<Boolean> = dataStore.audioQualitySaver
    override val downloadAudioOnly: Flow<Boolean> = dataStore.downloadAudioOnly
    override val streamAudioOnly: Flow<Boolean> = dataStore.streamAudioOnly

    override val notifRecommended: Flow<Boolean> = dataStore.notifRecommended
    override val notifNewMusic: Flow<Boolean> = dataStore.notifNewMusic
    override val notifPlaylist: Flow<Boolean> = dataStore.notifPlaylist
    override val notifConcert: Flow<Boolean> = dataStore.notifConcert
    override val notifArtist: Flow<Boolean> = dataStore.notifArtist
    override val notifNews: Flow<Boolean> = dataStore.notifNews
    override val notifEvents: Flow<Boolean> = dataStore.notifEvents

    override suspend fun setDarkMode(enabled: Boolean) = dataStore.setDarkMode(enabled)
    override suspend fun setLanguage(lang: String) = dataStore.setLanguage(lang)
    
    override suspend fun setWifiAudio(v: String) = dataStore.setWifiAudio(v)
    override suspend fun setCellularAudio(v: String) = dataStore.setCellularAudio(v)
    override suspend fun setAutoAdjustQuality(v: Boolean) = dataStore.setAutoAdjustQuality(v)
    override suspend fun setDownloadQuality(v: String) = dataStore.setDownloadQuality(v)

    override suspend fun setGapless(v: Boolean) = dataStore.setGapless(v)
    override suspend fun setAutomix(v: Boolean) = dataStore.setAutomix(v)
    override suspend fun setExplicit(v: Boolean) = dataStore.setExplicit(v)
    override suspend fun setNormalize(v: Boolean) = dataStore.setNormalize(v)

    override suspend fun setRememberMe(v: Boolean) = dataStore.setRememberMe(v)
    override suspend fun setFaceId(v: Boolean) = dataStore.setFaceId(v)
    override suspend fun setBiometricId(v: Boolean) = dataStore.setBiometricId(v)

    override suspend fun setAudioQualitySaver(v: Boolean) = dataStore.setAudioQualitySaver(v)
    override suspend fun setDownloadAudioOnly(v: Boolean) = dataStore.setDownloadAudioOnly(v)
    override suspend fun setStreamAudioOnly(v: Boolean) = dataStore.setStreamAudioOnly(v)

    override suspend fun setNotifRecommended(v: Boolean) = dataStore.setNotifRecommended(v)
    override suspend fun setNotifNewMusic(v: Boolean) = dataStore.setNotifNewMusic(v)
    override suspend fun setNotifPlaylist(v: Boolean) = dataStore.setNotifPlaylist(v)
    override suspend fun setNotifConcert(v: Boolean) = dataStore.setNotifConcert(v)
    override suspend fun setNotifArtist(v: Boolean) = dataStore.setNotifArtist(v)
    override suspend fun setNotifNews(v: Boolean) = dataStore.setNotifNews(v)
    override suspend fun setNotifEvents(v: Boolean) = dataStore.setNotifEvents(v)

    override suspend fun clearAll() = dataStore.clearAll()
}
