package com.taher.beatly.ui.artist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.domain.repository.PlayerRepository
import com.taher.beatly.domain.usecase.ToggleLikeUseCase
import com.taher.beatly.model.Artist
import com.taher.beatly.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ArtistDetailUiState(
    val artist: Artist? = null,
    val isLoading: Boolean = true,
)

@HiltViewModel
class ArtistDetailViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    private val toggleLikeUseCase: ToggleLikeUseCase,
    savedStateHandle: androidx.lifecycle.SavedStateHandle
) : ViewModel() {

    private val artistId: String = checkNotNull(savedStateHandle["artistId"])

    private val _uiState = MutableStateFlow(ArtistDetailUiState())
    val uiState: StateFlow<ArtistDetailUiState> = _uiState.asStateFlow()

    init {
        loadArtist()
    }

    private fun loadArtist() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val artistResult = musicRepository.getArtistDetail(artistId)
            val tracksResult = musicRepository.getArtistTopTracks(artistId)

            if (artistResult is BeatlyResult.Success) {
                val da = artistResult.data
                val domainSongs = (tracksResult as? BeatlyResult.Success)?.data ?: emptyList()
                val songs = domainSongs.map { it.toUi() }

                val modelArtist = Artist(
                    id = da.id,
                    name = da.name,
                    imageUrl = da.imageUrl,
                    isVerified = da.isVerified,
                    isFollowing = da.isFollowing,
                    monthlyListeners = da.monthlyListeners,
                    popularSongs = songs
                )
                _uiState.update { it.copy(artist = modelArtist, isLoading = false) }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onFollowClick() {
        viewModelScope.launch { musicRepository.toggleFollowArtist(artistId) }
    }

    fun onLikeSongToggled(songId: String) {
        viewModelScope.launch {
            toggleLikeUseCase(songId)
            _uiState.update { state ->
                state.copy(
                    artist = state.artist?.copy(
                        popularSongs = state.artist.popularSongs.map {
                            if (it.id == songId) it.copy(isLiked = !it.isLiked) else it
                        }
                    )
                )
            }
        }
    }

    fun onPlaySong(song: com.taher.beatly.model.Song) {
        // Player integration needed
    }
}
