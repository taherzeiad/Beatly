package com.taher.beatly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.data.MusicRepository
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val userName: String = "",
    val trendingSongs: List<Song> = emptyList(),
    val topArtists: List<Artist> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val currentlyPlayingSongId: String? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getUserName(),
                repository.getTrendingSongs(),
                repository.getTopArtists(),
                repository.getRecentlyPlayed()
            ) { userName, trending, artists, recent ->
                HomeUiState(
                    userName = userName,
                    trendingSongs = trending,
                    topArtists = artists,
                    recentlyPlayed = recent,
                    currentlyPlayingSongId = recent.firstOrNull()?.id,
                    isLoading = false
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun onLikeToggled(songId: String) {
        viewModelScope.launch { repository.toggleLikeSong(songId) }
    }

    fun onPlayPauseToggled(song: Song) {
        viewModelScope.launch {
            if (_uiState.value.currentlyPlayingSongId == song.id) {
                repository.togglePlayPause()
            } else {
                repository.playSong(song)
            }
            _uiState.value = _uiState.value.copy(currentlyPlayingSongId = song.id)
        }
    }
}
