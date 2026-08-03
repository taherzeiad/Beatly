package com.taher.beatly.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExploreUiState(
    val trendingSongs: List<Song> = emptyList(),
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
            when (val result = musicRepository.getTrendingSongs()) {
                is BeatlyResult.Success -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        trendingSongs = result.data.map { it.toUi() }
                    )}
                }
                is BeatlyResult.Error -> {
                    _uiState.update { it.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )}
                }
                else -> {
                    _uiState.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onPlaySong(song: Song) {
        viewModelScope.launch {
            val songs = _uiState.value.trendingSongs
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
