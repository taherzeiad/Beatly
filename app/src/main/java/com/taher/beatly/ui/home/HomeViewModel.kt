package com.taher.beatly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.Song as DomainSong
import com.taher.beatly.domain.model.Artist as DomainArtist
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.domain.repository.PlayerRepository
import com.taher.beatly.domain.usecase.GetHomeDataUseCase
import com.taher.beatly.domain.usecase.ToggleLikeUseCase
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

data class HomeUiState(
    val greeting: String = "",
    val userName: String = "",
    val trendingSongs: List<DomainSong> = emptyList(),
    val topArtists: List<DomainArtist> = emptyList(),
    val recentlyPlayed: List<DomainSong> = emptyList(),
    val currentSong: DomainSong? = null,
    val currentlyPlayingSongId: String? = null,
    val isPlaying: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    private val authRepository: AuthRepository,
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val toggleLikeUseCase: ToggleLikeUseCase,
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
                _uiState.update { it.copy(topArtists = artists) }
            }
        }
    }

    private fun observePlayerState() {
        viewModelScope.launch {
            playerRepository.playerState.collectLatest { state ->
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
                        _uiState.update { it.copy(recentlyPlayed = domainSongs) }
                    }
                }
            }
        }
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = getHomeDataUseCase()

                _uiState.update { state ->
                    when (result) {
                        is BeatlyResult.Success -> state.copy(
                            isLoading = false,
                            trendingSongs = result.data.trendingSongs,
                            topArtists = result.data.topArtists
                        )
                        is BeatlyResult.Error -> state.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                        else -> state
                    }
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

    fun onPlayPauseToggled(songId: String) {
        viewModelScope.launch {
            if (uiState.value.currentlyPlayingSongId == songId) {
                playerRepository.togglePlayPause()
            } else {
                val songs = uiState.value.trendingSongs
                val index = songs.indexOfFirst { it.id == songId }.coerceAtLeast(0)
                if (songs.isNotEmpty()) {
                    playerRepository.playQueue(songs, index)
                }
            }
        }
    }

    fun onLikeToggled(id: String) {
        viewModelScope.launch {
            toggleLikeUseCase(id)
        }
    }

    private fun getGreeting() = when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good Morning!"
        in 12..17 -> "Good Afternoon!"
        in 18..21 -> "Good Evening!"
        else -> "Good Night!"
    }
}
