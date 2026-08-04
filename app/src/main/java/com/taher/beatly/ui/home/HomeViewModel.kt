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
    private val musicRepository: MusicRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeUser()
        observePlayerState()
        observeTopArtists()
        loadHomeData()
    }

    private fun observeTopArtists() {
        viewModelScope.launch {
            musicRepository.getUserTopArtists().collectLatest { artists ->
                _uiState.update { it.copy(topArtists = artists.map { it.toUi() }) }
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            musicRepository.playerState.collectLatest { state ->
                _uiState.update {
                    it.copy(
                        currentSong = state.currentSong,
                        currentlyPlayingSongId = state.currentSong?.id,
                        isPlaying = state.isPlaying
                    )
                }
            }
        }
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
                        val uiSongs = domainSongs.map { it.toUi() }
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

                val t = trending.await()

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        trendingSongs = (t as? BeatlyResult.Success)?.data?.map { it.toUi() } ?: state.trendingSongs,
                        errorMessage = (t as? BeatlyResult.Error)?.message ?: state.errorMessage,
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

    fun onPlayPauseToggled(song: Song) {
        viewModelScope.launch {
            if (uiState.value.currentlyPlayingSongId == song.id) {
                musicRepository.togglePlayPause()
            } else {
                val songs = uiState.value.trendingSongs
                val index = songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
                if (songs.isNotEmpty()) {
                    musicRepository.playQueue(songs, index)
                } else {
                    musicRepository.playSong(song)
                }
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

// ── UI Mappers ─────────────────────────────────────────────────────────────

    private fun com.taher.beatly.domain.model.Song.toUi() = Song(
        id = id,
        title = title,
        artistName = artistName,
        artistId = artistId,
        imageUrl = imageUrl,
        previewUrl = previewUrl,
        durationMs = durationMs,
        isLiked = isLiked,
        isSaved = isSaved,
    )

    private fun com.taher.beatly.domain.model.Artist.toUi() = Artist(
        id = id,
        name = name,
        imageUrl = imageUrl,
        isVerified = isVerified,
        isFollowing = isFollowing,
        monthlyListeners = monthlyListeners,
    )
