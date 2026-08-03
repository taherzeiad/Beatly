package com.taher.beatly.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 1L,
    val error: String? = null
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    val uiState: StateFlow<PlayerUiState> = repository.playerState.map { state ->
        PlayerUiState(
            song = state.currentSong,
            isPlaying = state.isPlaying,
            positionMs = state.positionMs,
            durationMs = if (state.durationMs > 0) state.durationMs else 1L,
            error = state.error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerUiState()
    )

    fun onPlayPauseClicked() {
        viewModelScope.launch { repository.togglePlayPause() }
    }

    fun onSeek(positionMs: Long) {
        viewModelScope.launch {
            repository.seekTo(positionMs)
        }
    }

    fun onSeekForward() {
        viewModelScope.launch { repository.seekForward() }
    }

    fun onSeekBackward() {
        viewModelScope.launch { repository.seekBackward() }
    }

    fun onSkipNext() {
        viewModelScope.launch { repository.skipNext() }
    }

    fun onSkipPrevious() {
        viewModelScope.launch { repository.skipPrevious() }
    }
}