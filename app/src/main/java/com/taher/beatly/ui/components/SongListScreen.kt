package com.taher.beatly.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

enum class SongListSource { PLAYLIST, ALBUM, GENRE, RECENT }

data class SongListUiState(
    val title: String = "",
    val songs: List<Song> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@HiltViewModel
class SongListViewModel @Inject constructor(
    private val repository: MusicRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val source: SongListSource = SongListSource.valueOf(checkNotNull(savedStateHandle["source"]))
    private val id: String = checkNotNull(savedStateHandle["id"])
    private val initialTitle: String = savedStateHandle["title"] ?: "Songs"

    private val _uiState = MutableStateFlow(SongListUiState(title = initialTitle))
    val uiState: StateFlow<SongListUiState> = _uiState.asStateFlow()

    init {
        loadSongs()
    }

    private fun loadSongs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = when (source) {
                SongListSource.PLAYLIST -> repository.getPlaylistTracks(id)
                SongListSource.ALBUM -> repository.getAlbumTracks(id)
                SongListSource.GENRE -> repository.getGenreTracks(id)
                SongListSource.RECENT -> {
                    val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid
                    if (user != null) repository.getRecentlyPlayed(user)
                    else BeatlyResult.Success(emptyList())
                }
            }
            
            _uiState.update { state ->
                when (result) {
                    is BeatlyResult.Success -> state.copy(
                        isLoading = false,
                        songs = result.data.map { ds ->
                            Song(
                                id = ds.id, title = ds.title, artistName = ds.artistName,
                                artistId = ds.artistId, imageUrl = ds.imageUrl,
                                durationMs = ds.durationMs, isLiked = ds.isLiked, isSaved = ds.isSaved
                            )
                        }
                    )
                    is BeatlyResult.Error -> state.copy(isLoading = false, errorMessage = result.message)
                    else -> state.copy(isLoading = false)
                }
            }
        }
    }

    fun onPlaySong(song: Song) {
        viewModelScope.launch { repository.playSong(song) }
    }

    fun onLikeToggled(songId: String) {
        viewModelScope.launch {
            repository.toggleLikeSong(songId)
            _uiState.update { s ->
                s.copy(songs = s.songs.map { if (it.id == songId) it.copy(isLiked = !it.isLiked) else it })
            }
        }
    }
}

@Composable
fun SongListScreen(
    onBackClick: () -> Unit,
    onSongClick: (Song) -> Unit,
    viewModel: SongListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            BeatlyTopBar(
                title = uiState.title,
                onBackClick = onBackClick
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.songs, key = { it.id }) { song ->
                    SongRow(
                        song = song,
                        onLikeClick = { viewModel.onLikeToggled(song.id) },
                        onPlayClick = {
                            viewModel.onPlaySong(song)
                            onSongClick(song)
                        }
                    )
                }
            }
        }
    }
}
