package com.taher.beatly.ui.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.data.MusicRepository
import com.taher.beatly.model.LibraryFilter
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.Song
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LibraryUiState(
    val searchQuery: String = "",
    val selectedFilter: LibraryFilter = LibraryFilter.SONGS,
    val isAscending: Boolean = true,
    val items: List<LibraryItem> = emptyList(),
    val isCreateDialogVisible: Boolean = false,
    val newLibraryName: String = ""
)

data class LikedSongsUiState(
    val songs: List<Song> = emptyList(),
    val currentlyPlayingSongId: String? = null
)

/**
 * Backs both the "My Library" list screen and the "Liked Songs" sub-screen,
 * since they share the same lifecycle scope when navigating within Library.
 */
@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(LibraryFilter.SONGS)
    private val _isAscending = MutableStateFlow(true)
    private val _isCreateDialogVisible = MutableStateFlow(false)
    private val _newLibraryName = MutableStateFlow("")

    val uiState: StateFlow<LibraryUiState> = combine(
        repository.getLibraryItems(),
        _searchQuery,
        _selectedFilter,
        _isAscending,
        _isCreateDialogVisible
    ) { items, query, filter, ascending, dialogVisible ->
        val filtered = if (query.isBlank()) items else items.filter {
            it.name.contains(query, ignoreCase = true)
        }
        val sorted = if (ascending) filtered.sortedBy { it.name } else filtered.sortedByDescending { it.name }
        LibraryUiState(
            searchQuery = query,
            selectedFilter = filter,
            isAscending = ascending,
            items = sorted,
            isCreateDialogVisible = dialogVisible,
            newLibraryName = _newLibraryName.value
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LibraryUiState())

    val likedSongsState: StateFlow<LikedSongsUiState> = repository.getLikedSongs()
        .map { LikedSongsUiState(songs = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LikedSongsUiState())

    fun onSearchQueryChanged(query: String) { _searchQuery.value = query }
    fun onFilterSelected(filter: LibraryFilter) { _selectedFilter.value = filter }
    fun onToggleSortOrder() { _isAscending.value = !_isAscending.value }

    fun onAddClicked() { _isCreateDialogVisible.value = true }
    fun onDialogDismiss() {
        _isCreateDialogVisible.value = false
        _newLibraryName.value = ""
    }
    fun onNewLibraryNameChanged(name: String) { _newLibraryName.value = name }
    fun onCreateLibraryConfirmed() {
        viewModelScope.launch {
            repository.createLibraryPlaylist(_newLibraryName.value)
            onDialogDismiss()
        }
    }

    fun onLikeToggled(songId: String) {
        viewModelScope.launch { repository.toggleLikeSong(songId) }
    }

    fun onPlaySong(song: Song) {
        viewModelScope.launch { repository.playSong(song) }
    }
}
