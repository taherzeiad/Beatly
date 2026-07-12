package com.taher.beatly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Language ───────────────────────────────────────────────────────────────

data class LanguageGroup(val groupTitle: String, val languages: List<String>)

data class LanguageUiState(
    val groups: List<LanguageGroup> = emptyList(),
    val selectedLang: String = "English (US)"
)

@HiltViewModel
class LanguageViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUiState())
    val uiState: StateFlow<LanguageUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                groups = listOf(
                    LanguageGroup("Suggested", listOf("English (US)", "English (UK)")),
                    LanguageGroup(
                        "Others",
                        listOf("Mandarin", "Hindi", "Spanish", "Arabic", "French", "German")
                    ),
                )
            )
        }
        viewModelScope.launch {
            settingsRepository.language.collectLatest { lang ->
                _uiState.update { it.copy(selectedLang = lang) }
            }
        }
    }

    fun onLanguageSelected(lang: String) {
        viewModelScope.launch { settingsRepository.setLanguage(lang) }
    }
}

// ── Notification ───────────────────────────────────────────────────────────

data class NotificationItem(val id: String, val label: String, val subtitle: String = "Push, Email")
data class NotificationUiState(val items: List<NotificationItem> = emptyList())

@HiltViewModel
class NotificationViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                items = listOf(
                    NotificationItem("recommended", "Recommended Music"),
                    NotificationItem("new_music", "New Music"),
                    NotificationItem("playlist", "Playlist Updates"),
                    NotificationItem("concert", "Concert Notifications"),
                    NotificationItem("artist", "Artist Updates"),
                    NotificationItem("product_news", "Product News"),
                    NotificationItem("events", "Events"),
                )
            )
        }
    }
}

// ── Edit Profile ───────────────────────────────────────────────────────────

data class EditProfileUiState(
    val name: String = "",
    val username: String = "",
    val birthDate: String = "",
    val mail: String = "",
    val gender: String = "Male",
    val isValid: Boolean = true
)

@HiltViewModel
class EditProfileViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()
    fun onNameChanged(v: String) {
        _uiState.update { it.copy(name = v) }
    }

    fun onUsernameChanged(v: String) {
        _uiState.update { it.copy(username = v) }
    }

    fun onBirthDateChanged(v: String) {
        _uiState.update { it.copy(birthDate = v) }
    }

    fun onMailChanged(v: String) {
        _uiState.update { it.copy(mail = v) }
    }

    fun onGenderChanged(v: String) {
        _uiState.update { it.copy(gender = v) }
    }

    fun onUpdate() { /* TODO: call UserRepository */
    }
}

// ── Audio & Video ─────────────────────────────────────────────────────────

data class AudioVideoUiState(
    val wifiStreamingAudio: String = "High", val cellularStreamingAudio: String = "Automatic",
    val autoAdjustQuality: Boolean = true, val downloadQuality: String = "Normal",
    val wifiStreamingVideo: String = "High", val cellularStreamingVideo: String = "Medium"
)

@HiltViewModel
class AudioVideoViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AudioVideoUiState())
    val uiState: StateFlow<AudioVideoUiState> = _uiState.asStateFlow()
    fun onAutoAdjustToggled() {
        _uiState.update { it.copy(autoAdjustQuality = !it.autoAdjustQuality) }
    }

    fun onUpdate() {}
}

// ── Playback ───────────────────────────────────────────────────────────────

data class PlaybackSetting(
    val id: String,
    val label: String,
    val subtitle: String,
    val enabled: Boolean
)

data class PlaybackUiState(val settings: List<PlaybackSetting> = emptyList())

@HiltViewModel
class PlaybackViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    init {
        _uiState.update {
            it.copy(
                settings = listOf(
                    PlaybackSetting("gapless", "Gapless", "Allows gapless playback.", true),
                    PlaybackSetting(
                        "automixte",
                        "Automixte",
                        "Transitions between songs on select playlists.",
                        true
                    ),
                    PlaybackSetting(
                        "explicit",
                        "Allow Explicit Content",
                        "Turn on play explicit content.",
                        false
                    ),
                    PlaybackSetting(
                        "normalize",
                        "Normalize Volume",
                        "Set the same volume level for all tracks.",
                        true
                    ),
                    PlaybackSetting(
                        "canvas",
                        "Canvas",
                        "Display short, looping visuals on tracks.",
                        false
                    ),
                    PlaybackSetting(
                        "broadcast",
                        "Device Broadcast Status",
                        "Allow other apps on your device to see what you are listening to.",
                        true
                    ),
                )
            )
        }
    }

    fun onToggled(id: String) {
        _uiState.update { s -> s.copy(settings = s.settings.map { if (it.id == id) it.copy(enabled = !it.enabled) else it }) }
    }
}

// ── Data Saver ─────────────────────────────────────────────────────────────

data class DataSaverUiState(
    val audioQualitySaver: Boolean = true,
    val downloadAudioOnly: Boolean = true,
    val streamAudioOnly: Boolean = true,
    val otherAppsStorage: String = "75.4 GB",
    val cacheStorage: String = "120.6 MB"
)

@HiltViewModel
class DataSaverViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(DataSaverUiState())
    val uiState: StateFlow<DataSaverUiState> = _uiState.asStateFlow()
    fun onAudioQualityToggled() {
        _uiState.update { it.copy(audioQualitySaver = !it.audioQualitySaver) }
    }

    fun onDownloadAudioToggled() {
        _uiState.update { it.copy(downloadAudioOnly = !it.downloadAudioOnly) }
    }

    fun onStreamAudioToggled() {
        _uiState.update { it.copy(streamAudioOnly = !it.streamAudioOnly) }
    }
}

// ── Security ───────────────────────────────────────────────────────────────

data class SecurityUiState(
    val rememberMe: Boolean = true,
    val faceId: Boolean = false,
    val biometricId: Boolean = true
)

@HiltViewModel
class SecurityViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()
    fun onRememberMeToggled() {
        _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun onFaceIdToggled() {
        _uiState.update { it.copy(faceId = !it.faceId) }
    }

    fun onBiometricToggled() {
        _uiState.update { it.copy(biometricId = !it.biometricId) }
    }

    fun onChangePin() { /* TODO */
    }

    fun onChangePassword() { /* TODO */
    }
}
