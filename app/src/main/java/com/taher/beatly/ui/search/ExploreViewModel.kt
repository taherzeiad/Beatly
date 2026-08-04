package com.taher.beatly.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val justForYouSongs: List<Song> = emptyList(),
    val topSongs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val musicRepository: MusicRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadExploreData()
    }

    private fun loadExploreData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val recommendedJob = async { musicRepository.getRecommendedSongs() }
                val trendingJob = async { musicRepository.getTrendingSongs() }

                val recommended = recommendedJob.await()
                val trending = trendingJob.await()

                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        justForYouSongs = (recommended as? BeatlyResult.Success)?.data?.map { it.toUi() } ?: state.justForYouSongs,
                        topSongs = (trending as? BeatlyResult.Success)?.data?.map { it.toUi() } ?: state.topSongs,
                        errorMessage = (recommended as? BeatlyResult.Error)?.message 
                            ?: (trending as? BeatlyResult.Error)?.message 
                            ?: state.errorMessage
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onPlaySong(song: Song, fromSection: String) {
        viewModelScope.launch {
            val songs = when (fromSection) {
                "just_for_you" -> _uiState.value.justForYouSongs
                "top_songs" -> _uiState.value.topSongs
                else -> listOf(song)
            }
            val index = songs.indexOfFirst { it.id == song.id }.coerceAtLeast(0)
            if (songs.isNotEmpty()) {
                musicRepository.playQueue(songs, index)
            } else {
                musicRepository.playSong(song)
            }
        }
    }

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
}
