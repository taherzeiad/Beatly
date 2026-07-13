package com.taher.beatly.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
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
        loadArtist()
    }

    private fun loadArtist() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val artistResult = repository.getArtistDetail(artistId)
            val tracksResult = repository.getArtistTopTracks(artistId)

            if (artistResult is BeatlyResult.Success) {
                val da = artistResult.data
                val songs = if (tracksResult is BeatlyResult.Success) {
                    tracksResult.data.map { ds ->
                        com.taher.beatly.model.Song(
                            id = ds.id, title = ds.title, artistName = ds.artistName,
                            artistId = ds.artistId, imageUrl = ds.imageUrl,
                            durationMs = ds.durationMs, isLiked = ds.isLiked, isSaved = ds.isSaved
                        )
                    }
                } else emptyList()

                val modelArtist = Artist(
                    id = da.id,
                    name = da.name,
                    imageUrl = da.imageUrl,
                    isVerified = da.isVerified,
                    isFollowing = da.isFollowing,
                    monthlyListeners = da.monthlyListeners,
                    popularSongs = songs
                )
                _uiState.value = ArtistDetailUiState(artist = modelArtist, isLoading = false)
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
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
