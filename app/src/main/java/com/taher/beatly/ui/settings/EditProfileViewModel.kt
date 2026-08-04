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

data class NotificationItem(
    val id: String,
    val labelRes: Int,
    val subtitle: String = "Push, Email",
    val enabled: Boolean = true
)

data class NotificationUiState(
    val items: List<NotificationItem> = emptyList(),
    val success: Boolean = false
)

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        combine(
            settingsRepository.notifRecommended,
            settingsRepository.notifNewMusic,
            settingsRepository.notifPlaylist,
            settingsRepository.notifConcert,
            settingsRepository.notifArtist,
            settingsRepository.notifNews,
            settingsRepository.notifEvents
        ) { values ->
            listOf(
                NotificationItem("recommended",  com.taher.beatly.R.string.notif_recommended,  enabled = values[0]),
                NotificationItem("new_music",   com.taher.beatly.R.string.notif_new_music,    enabled = values[1]),
                NotificationItem("playlist",    com.taher.beatly.R.string.notif_playlist,     enabled = values[2]),
                NotificationItem("concert",     com.taher.beatly.R.string.notif_concert,      enabled = values[3]),
                NotificationItem("artist",      com.taher.beatly.R.string.notif_artist,       enabled = values[4]),
                NotificationItem("product_news", com.taher.beatly.R.string.notif_product_news,  enabled = values[5]),
                NotificationItem("events",       com.taher.beatly.R.string.notif_events,        enabled = values[6]),
            )
        }.onEach { items ->
            _uiState.update { it.copy(items = items) }
        }.launchIn(viewModelScope)
    }

    fun onToggled(id: String, enabled: Boolean) {
        viewModelScope.launch {
            when (id) {
                "recommended"  -> settingsRepository.setNotifRecommended(enabled)
                "new_music"    -> settingsRepository.setNotifNewMusic(enabled)
                "playlist"     -> settingsRepository.setNotifPlaylist(enabled)
                "concert"      -> settingsRepository.setNotifConcert(enabled)
                "artist"       -> settingsRepository.setNotifArtist(enabled)
                "product_news" -> settingsRepository.setNotifNews(enabled)
                "events"       -> settingsRepository.setNotifEvents(enabled)
            }
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
    val avatarUrl: String = "",
    val isDarkMode: Boolean = false,
    val isLoading: Boolean = false,
    val success: Boolean = false
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val settingsRepository: SettingsRepository
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
                    mail = user.email,
                    birthDate = user.birthDate,
                    avatarUrl = user.avatarUrl,
                    gender = if (user.gender.isNotEmpty()) user.gender else it.gender
                )}
            }
        }
        viewModelScope.launch {
            settingsRepository.isDarkMode.collectLatest { isDark ->
                _uiState.update { it.copy(isDarkMode = isDark) }
            }
        }
    }

    fun onNameChanged(v: String)     { _uiState.update { it.copy(name = v) } }
    fun onUsernameChanged(v: String) { _uiState.update { it.copy(username = v) } }
    fun onBirthDateChanged(v: String){ _uiState.update { it.copy(birthDate = v) } }
    fun onMailChanged(v: String)     { _uiState.update { it.copy(mail = v) } }
    fun onGenderChanged(v: String)   { _uiState.update { it.copy(gender = v) } }

    fun onDarkModeToggled() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(!_uiState.value.isDarkMode)
        }
    }

    fun onUpdate() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val result = userRepository.updateProfile(
                User(
                    id = state.id,
                    name = state.name,
                    email = state.mail,
                    username = state.username,
                    birthDate = state.birthDate,
                    gender = state.gender,
                    avatarUrl = state.avatarUrl
                )
            )
            _uiState.update { it.copy(isLoading = false, success = result is BeatlyResult.Success) }
        }
    }
}

// ── Audio & Video ─────────────────────────────────────────────────────────

data class AudioVideoUiState(
    val wifiStreamingAudio: String = "High", 
    val cellularStreamingAudio: String = "Automatic",
    val autoAdjustQuality: Boolean = true, 
    val downloadQuality: String = "Normal",
    val wifiStreamingVideo: String = "High", 
    val cellularStreamingVideo: String = "Medium",
    val isQualityDialogVisible: Boolean = false,
    val activeSelectionKey: String = "", // "wifi", "cellular", "download"
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
            settingsRepository.downloadQuality
        ) { wifi, cellular, auto, download ->
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

    fun onQualityRowClicked(key: String) {
        _uiState.update { it.copy(isQualityDialogVisible = true, activeSelectionKey = key) }
    }

    fun onQualityDialogDismiss() {
        _uiState.update { it.copy(isQualityDialogVisible = false, activeSelectionKey = "") }
    }

    fun onQualitySelected(quality: String) {
        viewModelScope.launch {
            when (_uiState.value.activeSelectionKey) {
                "wifi" -> settingsRepository.setWifiAudio(quality)
                "cellular" -> settingsRepository.setCellularAudio(quality)
                "download" -> settingsRepository.setDownloadQuality(quality)
            }
            onQualityDialogDismiss()
        }
    }

    fun onUpdate() {
        _uiState.update { it.copy(success = true) }
    }
}

// ── Playback ───────────────────────────────────────────────────────────────

data class PlaybackSetting(
    val id: String,
    val labelRes: Int,
    val subtitleRes: Int,
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
        ) { values ->
            listOf(
                PlaybackSetting("gapless",   com.taher.beatly.R.string.gapless,   com.taher.beatly.R.string.gapless_desc,   values[0]),
                PlaybackSetting("automix",   com.taher.beatly.R.string.automix,   com.taher.beatly.R.string.automix_desc,   values[1]),
                PlaybackSetting("explicit",  com.taher.beatly.R.string.explicit,  com.taher.beatly.R.string.explicit_desc,  values[2]),
                PlaybackSetting("normalize", com.taher.beatly.R.string.normalize, com.taher.beatly.R.string.normalize_desc, values[3]),
                PlaybackSetting("canvas",    com.taher.beatly.R.string.canvas,    com.taher.beatly.R.string.canvas_desc,    false),
                PlaybackSetting("broadcast", com.taher.beatly.R.string.broadcast, com.taher.beatly.R.string.broadcast_desc, true),
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
class DataSaverViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DataSaverUiState())
    val uiState: StateFlow<DataSaverUiState> = _uiState.asStateFlow()

    init {
        combine(
            settingsRepository.audioQualitySaver,
            settingsRepository.downloadAudioOnly,
            settingsRepository.streamAudioOnly
        ) { audio, download, stream ->
            DataSaverUiState(
                audioQualitySaver = audio,
                downloadAudioOnly = download,
                streamAudioOnly = stream
            )
        }.onEach { state ->
            _uiState.update { it.copy(
                audioQualitySaver = state.audioQualitySaver,
                downloadAudioOnly = state.downloadAudioOnly,
                streamAudioOnly = state.streamAudioOnly
            )}
        }.launchIn(viewModelScope)
    }

    fun onAudioQualityToggled() {
        viewModelScope.launch {
            settingsRepository.setAudioQualitySaver(!_uiState.value.audioQualitySaver)
        }
    }

    fun onDownloadAudioToggled() {
        viewModelScope.launch {
            settingsRepository.setDownloadAudioOnly(!_uiState.value.downloadAudioOnly)
        }
    }

    fun onStreamAudioToggled() {
        viewModelScope.launch {
            settingsRepository.setStreamAudioOnly(!_uiState.value.streamAudioOnly)
        }
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


    fun onUpdate() { _uiState.update { it.copy(success = true) } }
}
