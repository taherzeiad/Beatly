package com.taher.beatly.ui.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.data.MusicRepository
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val song: Song? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 1L
)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    // In a real app this would observe a shared PlaybackState flow exposed by
    // the repository/media session. Kept minimal here for clarity.
    private val _uiState = MutableStateFlow(
        PlayerUiState(
            song = Song(id = "s8", title = "Free Spirit", artistName = "Khalid", artistId = "a5"),
            isPlaying = true,
            positionMs = 215_000L,
            durationMs = 230_000L
        )
    )
    val uiState: StateFlow<PlayerUiState> = _uiState.asStateFlow()

    fun onPlayPauseClicked() {
        viewModelScope.launch {
            repository.togglePlayPause()
            _uiState.value = _uiState.value.copy(isPlaying = !_uiState.value.isPlaying)
        }
    }

    fun onSeek(positionMs: Long) {
        viewModelScope.launch {
            repository.seekTo(positionMs)
            _uiState.value = _uiState.value.copy(positionMs = positionMs)
        }
    }

    fun onSkipNext() { viewModelScope.launch { repository.skipNext() } }
    fun onSkipPrevious() { viewModelScope.launch { repository.skipPrevious() } }
}
