package com.taher.beatly.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.Artist
import com.taher.beatly.model.SearchFilter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ARTISTS,
    val artists: List<Artist> = emptyList(),
    val isLoading: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: MusicRepository
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.ARTISTS)
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<SearchUiState> = combine(
        _query, _selectedFilter, _artists, _isLoading
    ) { query, filter, artists, loading ->
        SearchUiState(query = query, selectedFilter = filter, artists = artists, isLoading = loading)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    init {
        observeSearch()
    }

    private fun observeSearch() {
        _query
            .debounce(250)
            .distinctUntilChanged()
            .onEach { _isLoading.value = true }
            .mapLatest { query -> repository.searchArtists(query) }
            .onEach { result ->
                _isLoading.value = false
                if (result is BeatlyResult.Success) {
                    // Map domain Artist to UI Artist if necessary
                    // Assuming for now they are compatible or I handle the mapping
                    // _artists.value = result.data.map { ... }
                    // Actually, the repository interface uses domain model Artist.
                    // Let's assume result.data is List<com.taher.beatly.domain.model.Artist>
                    // and I need to map to com.taher.beatly.model.Artist if they are different.
                    
                    val modelArtists = result.data.map { da ->
                        Artist(
                            id = da.id,
                            name = da.name,
                            imageUrl = da.imageUrl,
                            isVerified = da.isVerified,
                            isFollowing = da.isFollowing,
                            monthlyListeners = da.monthlyListeners
                        )
                    }
                    _artists.value = modelArtists
                }
            }
            .launchIn(viewModelScope)
    }

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
