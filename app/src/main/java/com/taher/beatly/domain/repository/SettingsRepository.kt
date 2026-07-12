package com.taher.beatly.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val isDarkMode: Flow<Boolean>
    val language: Flow<String>
    suspend fun setDarkMode(enabled: Boolean)
    suspend fun setLanguage(lang: String)
    suspend fun clearAll()
}
