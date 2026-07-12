package com.taher.beatly.ui.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ProfileSettingItem(
    val id      : String,
    val label   : String,
    val isToggle: Boolean = false,
    val toggled : Boolean = false
)

data class ProfileUiState(
    val userName  : String                 = "Jenny Wilson",
    val email     : String                 = "wilson9@gmail.com",
    val isDarkMode: Boolean                = false,
    val settings  : List<ProfileSettingItem> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(settings = listOf(
                ProfileSettingItem("profile",        "Profile"),
                ProfileSettingItem("notification",   "Notification"),
                ProfileSettingItem("dark_mode",      "Dark Mode", isToggle = true, toggled = false),
                ProfileSettingItem("audio_video",    "Audio & Video"),
                ProfileSettingItem("playback",       "Playback"),
                ProfileSettingItem("downloads",      "Data Saver & Storage"),
                ProfileSettingItem("security",       "Security"),
                ProfileSettingItem("language",       "Language"),
                ProfileSettingItem("privacy_policy", "Privacy Policy"),
                ProfileSettingItem("about",          "About"),
                ProfileSettingItem("delete_account", "Delete Account"),
                ProfileSettingItem("logout",         "Logout"),
            ))
        }
    }

    fun onDarkModeToggled() {
        _uiState.update { state ->
            state.copy(
                isDarkMode = !state.isDarkMode,
                settings   = state.settings.map { item ->
                    if (item.id == "dark_mode") item.copy(toggled = !item.toggled) else item
                }
            )
        }
    }

    fun onDeleteAccount() { /* TODO: call auth repository */ }
    fun onLogout()        { /* TODO: clear session + navigate to splash */ }
}
