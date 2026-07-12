package com.taher.beatly.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileSettingItem(
    val id      : String,
    val label   : String,
    val isToggle: Boolean = false,
    val toggled : Boolean = false
)

data class ProfileUiState(
    val userName  : String                 = "",
    val email     : String                 = "",
    val isDarkMode: Boolean                = false,
    val isLoggedOut: Boolean               = false,
    val settings  : List<ProfileSettingItem> = emptyList()
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
        observeUserData()
    }

    private fun loadSettings() {
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

    private fun observeUserData() {
        authRepository.currentUser
            .onEach { user ->
                _uiState.update { it.copy(
                    userName = user?.name ?: "Guest",
                    email = user?.email ?: ""
                )}
            }
            .launchIn(viewModelScope)

        settingsRepository.isDarkMode
            .onEach { darkMode ->
                _uiState.update { state ->
                    state.copy(
                        isDarkMode = darkMode,
                        settings = state.settings.map {
                            if (it.id == "dark_mode") it.copy(toggled = darkMode) else it
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onDarkModeToggled() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(!_uiState.value.isDarkMode)
        }
    }

    fun onDeleteAccount() {
        viewModelScope.launch {
            authRepository.deleteAccount()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.update { it.copy(isLoggedOut = true) }
        }
    }
}
