package com.taher.beatly.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.data.MusicRepository
import com.taher.beatly.model.Artist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistDetailUiState(
    val artist: Artist? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val repository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val artistId: String = checkNotNull(savedStateHandle["artistId"])

    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getArtistById(artistId).collect { artist ->
                _uiState.value = ArtistDetailUiState(artist = artist, isLoading = false)
            }
        }
    }

    fun onFollowClick() {
        viewModelScope.launch { repository.toggleFollowArtist(artistId) }
    }

    fun onLikeSongToggled(songId: String) {
        viewModelScope.launch { repository.toggleLikeSong(songId) }
    }

    fun onPlaySong(song: com.taher.beatly.model.Song) {
        viewModelScope.launch { repository.playSong(song) }
    }
}
