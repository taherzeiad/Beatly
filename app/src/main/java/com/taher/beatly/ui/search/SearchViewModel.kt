package com.taher.beatly.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.data.MusicRepository
import com.taher.beatly.model.Artist
import com.taher.beatly.model.SearchFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ARTISTS,
    val artists: List<Artist> = emptyList()
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.ARTISTS)

    private val artistResults = _query
        .debounce(250)
        .distinctUntilChanged()
        .flatMapLatest { repository.searchArtists(it) }

    val uiState: StateFlow<SearchUiState> = combine(
        _query, _selectedFilter, artistResults
    ) { query, filter, artists ->
        SearchUiState(query = query, selectedFilter = filter, artists = artists)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun onFilterSelected(filter: SearchFilter) {
        _selectedFilter.value = filter
    }

    fun onFollowToggled(artistId: String) {
        viewModelScope.launch { repository.toggleFollowArtist(artistId) }
    }
}
