package com.taher.beatly.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.repository.PlayerRepository
import com.taher.beatly.model.Song
import com.taher.beatly.ui.mapper.toUi
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
    private val playerRepository: PlayerRepository
) : ViewModel() {

    val uiState: StateFlow<PlayerUiState> = playerRepository.playerState.map { state ->
        PlayerUiState(
            song = state.currentSong?.toUi(),
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
        viewModelScope.launch { playerRepository.togglePlayPause() }
    }

    fun onSeek(positionMs: Long) {
        viewModelScope.launch {
            playerRepository.seekTo(positionMs)
        }
    }

    fun onSeekForward() {
        viewModelScope.launch { playerRepository.seekForward() }
    }

    fun onSeekBackward() {
        viewModelScope.launch { playerRepository.seekBackward() }
    }

    fun onSkipNext() {
        viewModelScope.launch { playerRepository.skipNext() }
    }

    fun onSkipPrevious() {
        viewModelScope.launch { playerRepository.skipPrevious() }
    }

    fun onErrorShown() {
        playerRepository.clearError()
    }
}
