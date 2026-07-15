package com.taher.beatly.ui.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "",
    val userName: String = "",
    val trendingSongs: List<Song> = emptyList(),
    val topArtists: List<Artist> = emptyList(),
    val recentlyPlayed: List<Song> = emptyList(),
    val currentSong: Song? = null,
    val currentlyPlayingSongId: String? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository, private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        loadHomeData()
    }

    private fun observeUser() {
        viewModelScope.launch {
            authRepository.currentUser.collectLatest { user ->
                _uiState.update {
                    it.copy(
                        userName = user?.name ?: "", greeting = getGreeting()
                    )
                }
                user?.id?.let { userId ->
                    musicRepository.getRecentlyPlayedFlow(userId).collectLatest { domainSongs ->
                        val uiSongs = domainSongs.map { ds ->
                            Song(
                                id = ds.id,
                                title = ds.title,
                                artistName = ds.artistName,
                                artistId = ds.artistId,
                                imageUrl = ds.imageUrl,
                                previewUrl = ds.previewUrl,
                                durationMs = ds.durationMs,
                                isLiked = ds.isLiked,
                                isSaved = ds.isSaved
                            )
                        }
                        _uiState.update { it.copy(recentlyPlayed = uiSongs) }
                    }
                }
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val trending = async { musicRepository.getTrendingSongs() }
                val artists = async { musicRepository.getTopArtists() }

                val t = trending.await()
                val a = artists.await()

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        trendingSongs = (if (t is BeatlyResult.Success) {
                            t.data.map { ds ->
                                Song(
                                    id = ds.id,
                                    title = ds.title,
                                    artistName = ds.artistName,
                                    artistId = ds.artistId,
                                    imageUrl = ds.imageUrl,
                                    durationMs = ds.durationMs,
                                    isLiked = ds.isLiked,
                                    isSaved = ds.isSaved
                                )
                            }
                        } else state.trendingSongs),
                        topArtists = (if (a is BeatlyResult.Success) {
                            a.data.map { da ->
                                Artist(
                                    id = da.id,
                                    name = da.name,
                                    imageUrl = da.imageUrl,
                                    isVerified = da.isVerified,
                                    isFollowing = da.isFollowing,
                                    monthlyListeners = da.monthlyListeners
                                )
                            }
                        } else state.topArtists),
                        errorMessage = if (t is BeatlyResult.Error) t.message else if (a is BeatlyResult.Error) a.message else null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    private fun loadRecentlyPlayed(userId: String) {
        viewModelScope.launch {
            when (val result = musicRepository.getRecentlyPlayed(userId)) {
                is BeatlyResult.Success -> {
                    val modelSongs = result.data.map { ds ->
                        Song(
                            id = ds.id,
                            title = ds.title,
                            artistName = ds.artistName,
                            artistId = ds.artistId,
                            imageUrl = ds.imageUrl,
                            durationMs = ds.durationMs,
                            isLiked = ds.isLiked,
                            isSaved = ds.isSaved
                        )
                    }
                    _uiState.update { it.copy(recentlyPlayed = modelSongs) }
                }

                else -> {}
            }
        }
    }

    fun onPlayPauseToggled(song: Song) {
        viewModelScope.launch {
            musicRepository.playSong(song)
            _uiState.update {
                it.copy(
                    currentSong = song, currentlyPlayingSongId = song.id, isPlaying = true
                )
            }
        }
    }

    fun onLikeToggled(id: String) {
        viewModelScope.launch {
            musicRepository.toggleLikeSong(id)
            // The UI state for recentlyPlayed will be refreshed if we observe a Flow,
            // but for now let's manually update it to show immediate feedback.
            _uiState.update { state ->
                state.copy(recentlyPlayed = state.recentlyPlayed.map {
                    if (it.id == id) it.copy(isLiked = !it.isLiked) else it
                }, trendingSongs = state.trendingSongs.map {
                    if (it.id == id) it.copy(isLiked = !it.isLiked) else it
                })
            }
        }
    }

    private fun getGreeting() = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning!"
        in 12..17 -> "Good Afternoon!"
        in 18..21 -> "Good Evening!"
        else -> "Good Night!"
    }
}
