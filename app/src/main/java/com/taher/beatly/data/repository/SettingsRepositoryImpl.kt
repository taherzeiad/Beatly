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
    override suspend fun setDarkMode(enabled: Boolean) = dataStore.setDarkMode(enabled)
    override suspend fun setLanguage(lang: String) = dataStore.setLanguage(lang)
    override suspend fun clearAll() = dataStore.clearAll()
}
