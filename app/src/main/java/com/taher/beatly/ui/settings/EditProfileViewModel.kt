package com.taher.beatly.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.User
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.SettingsRepository
import com.taher.beatly.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// ── Language ───────────────────────────────────────────────────────────────

data class LanguageGroup(val groupTitle: String, val languages: List<String>)

data class LanguageUiState(
    val groups: List<LanguageGroup> = emptyList(),
    val selectedLang: String = "English (US)",
    val success: Boolean = false
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
        viewModelScope.launch { 
            settingsRepository.setLanguage(lang)
            _uiState.update { it.copy(success = true) }
        }
    }
}

// ── Notification ───────────────────────────────────────────────────────────

data class NotificationItem(val id: String, val label: String, val subtitle: String = "Push, Email")
data class NotificationUiState(
    val items: List<NotificationItem> = emptyList(),
    val success: Boolean = false
)

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

    fun onUpdate() { _uiState.update { it.copy(success = true) } }
}

// ── Edit Profile ───────────────────────────────────────────────────────────

data class EditProfileUiState(
    val id: String = "",
    val name: String = "",
    val username: String = "",
    val birthDate: String = "",
    val mail: String = "",
    val gender: String = "Male",
    val isLoading: Boolean = false,
    val success: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.currentUser.filterNotNull().collectLatest { user ->
                _uiState.update { it.copy(
                    id = user.id,
                    name = user.name,
                    username = user.username,
                    mail = user.email
                )}
            }
        }
    }

    fun onNameChanged(v: String)     { _uiState.update { it.copy(name = v) } }
    fun onUsernameChanged(v: String) { _uiState.update { it.copy(username = v) } }
    fun onBirthDateChanged(v: String){ _uiState.update { it.copy(birthDate = v) } }
    fun onMailChanged(v: String)     { _uiState.update { it.copy(mail = v) } }
    fun onGenderChanged(v: String)   { _uiState.update { it.copy(gender = v) } }

    fun onUpdate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val result = userRepository.updateProfile(
                User(id = state.id, name = state.name, email = state.mail, username = state.username)
            )
            _uiState.update { it.copy(isLoading = false, success = result is BeatlyResult.Success) }
        }
    }
}

// ── Audio & Video ─────────────────────────────────────────────────────────

data class AudioVideoUiState(
    val wifiStreamingAudio: String = "High", val cellularStreamingAudio: String = "Automatic",
    val autoAdjustQuality: Boolean = true, val downloadQuality: String = "Normal",
    val wifiStreamingVideo: String = "High", val cellularStreamingVideo: String = "Medium",
    val success: Boolean = false
)

@HiltViewModel
class AudioVideoViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AudioVideoUiState())
    val uiState: StateFlow<AudioVideoUiState> = _uiState.asStateFlow()

    init {
        combine(
            settingsRepository.wifiAudio,
            settingsRepository.cellularAudio,
            settingsRepository.autoAdjustQuality,
            settingsRepository.downloadQuality,
            settingsRepository.isDarkMode // Just to have 5 flows if needed, or just combine 4
        ) { wifi, cellular, auto, download, _ ->
            AudioVideoUiState(
                wifiStreamingAudio = wifi,
                cellularStreamingAudio = cellular,
                autoAdjustQuality = auto,
                downloadQuality = download
            )
        }.onEach { state ->
            _uiState.update { it.copy(
                wifiStreamingAudio = state.wifiStreamingAudio,
                cellularStreamingAudio = state.cellularStreamingAudio,
                autoAdjustQuality = state.autoAdjustQuality,
                downloadQuality = state.downloadQuality
            )}
        }.launchIn(viewModelScope)
    }

    fun onAutoAdjustToggled() {
        viewModelScope.launch {
            settingsRepository.setAutoAdjustQuality(!_uiState.value.autoAdjustQuality)
        }
    }

    fun onUpdate() {
        _uiState.update { it.copy(success = true) }
    }
}

// ── Playback ───────────────────────────────────────────────────────────────

data class PlaybackSetting(
    val id: String,
    val label: String,
    val subtitle: String,
    val enabled: Boolean
)

data class PlaybackUiState(
    val settings: List<PlaybackSetting> = emptyList(),
    val success: Boolean = false
)

@HiltViewModel
class PlaybackViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PlaybackUiState())
    val uiState: StateFlow<PlaybackUiState> = _uiState.asStateFlow()

    init {
        combine(
            settingsRepository.gapless,
            settingsRepository.automix,
            settingsRepository.explicit,
            settingsRepository.normalize
        ) { gapless, automix, explicit, normalize ->
            listOf(
                PlaybackSetting("gapless", "Gapless", "Allows gapless playback.", gapless),
                PlaybackSetting("automix", "Automixte", "Transitions between songs on select playlists.", automix),
                PlaybackSetting("explicit", "Allow Explicit Content", "Turn on play explicit content.", explicit),
                PlaybackSetting("normalize", "Normalize Volume", "Set the same volume level for all tracks.", normalize),
                PlaybackSetting("canvas", "Canvas", "Display short, looping visuals on tracks.", false),
                PlaybackSetting("broadcast", "Device Broadcast Status", "Allow other apps on your device to see what you are listening to.", true),
            )
        }.onEach { settings ->
            _uiState.update { it.copy(settings = settings) }
        }.launchIn(viewModelScope)
    }

    fun onToggled(id: String) {
        viewModelScope.launch {
            when (id) {
                "gapless" -> settingsRepository.setGapless(!_uiState.value.settings.find { it.id == "gapless" }!!.enabled)
                "automix" -> settingsRepository.setAutomix(!_uiState.value.settings.find { it.id == "automix" }!!.enabled)
                "explicit" -> settingsRepository.setExplicit(!_uiState.value.settings.find { it.id == "explicit" }!!.enabled)
                "normalize" -> settingsRepository.setNormalize(!_uiState.value.settings.find { it.id == "normalize" }!!.enabled)
            }
        }
    }

    fun onUpdate() { _uiState.update { it.copy(success = true) } }
}

// ── Data Saver ─────────────────────────────────────────────────────────────

data class DataSaverUiState(
    val audioQualitySaver: Boolean = true,
    val downloadAudioOnly: Boolean = true,
    val streamAudioOnly: Boolean = true,
    val otherAppsStorage: String = "75.4 GB",
    val cacheStorage: String = "120.6 MB",
    val success: Boolean = false
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

    fun onUpdate() { _uiState.update { it.copy(success = true) } }
}

// ── Security ───────────────────────────────────────────────────────────────

data class SecurityUiState(
    val rememberMe: Boolean = true,
    val faceId: Boolean = false,
    val biometricId: Boolean = true,
    val success: Boolean = false
)

@HiltViewModel
class SecurityViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState: StateFlow<SecurityUiState> = _uiState.asStateFlow()

    init {
        combine(
            settingsRepository.rememberMe,
            settingsRepository.faceId,
            settingsRepository.biometricId
        ) { rememberMe, faceId, biometricId ->
            SecurityUiState(
                rememberMe = rememberMe,
                faceId = faceId,
                biometricId = biometricId
            )
        }.onEach { state ->
            _uiState.update { state }
        }.launchIn(viewModelScope)
    }

    fun onRememberMeToggled() {
        viewModelScope.launch { settingsRepository.setRememberMe(!_uiState.value.rememberMe) }
    }

    fun onFaceIdToggled() {
        viewModelScope.launch { settingsRepository.setFaceId(!_uiState.value.faceId) }
    }

    fun onBiometricToggled() {
        viewModelScope.launch { settingsRepository.setBiometricId(!_uiState.value.biometricId) }
    }

    fun onChangePin() { /* TODO */ }
    fun onChangePassword() { /* TODO */ }

    fun onUpdate() { _uiState.update { it.copy(success = true) } }
}
