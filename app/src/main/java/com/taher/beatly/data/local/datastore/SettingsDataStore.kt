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
        val KEY_AUTH_TOKEN= stringPreferencesKey("auth_token")
    }

    // ── Dark mode ──────────────────────────────────────────────────────────
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .catch { emit(emptyPreferences()) }
        .map   { it[KEY_DARK_MODE] ?: false }

    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { it[KEY_DARK_MODE] = enabled }
    }

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