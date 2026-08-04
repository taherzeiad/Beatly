package com.taher.beatly.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.domain.repository.PlayerRepository
import com.taher.beatly.domain.usecase.SearchUseCase
import com.taher.beatly.model.Album
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Playlist
import com.taher.beatly.model.SearchFilter
import com.taher.beatly.ui.mapper.toUi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

data class SearchUiState(
    val query: String = "",
    val selectedFilter: SearchFilter = SearchFilter.ARTISTS,
    val artists: List<Artist> = emptyList(),
    val songs: List<com.taher.beatly.model.Song> = emptyList(),
    val albums: List<Album> = emptyList(),
    val playlists: List<Playlist> = emptyList(),
    val isLoading: Boolean = false,
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val musicRepository: MusicRepository,
    private val playerRepository: PlayerRepository,
    private val searchUseCase: SearchUseCase,
    private val toggleLikeUseCase: com.taher.beatly.domain.usecase.ToggleLikeUseCase
) : ViewModel() {

    private val _query = MutableStateFlow("")
    private val _selectedFilter = MutableStateFlow(SearchFilter.ARTISTS)
    private val _artists = MutableStateFlow<List<Artist>>(emptyList())
    private val _domainSongs = MutableStateFlow<List<com.taher.beatly.domain.model.Song>>(emptyList())
    private val _songs = MutableStateFlow<List<com.taher.beatly.model.Song>>(emptyList())
    private val _albums = MutableStateFlow<List<Album>>(emptyList())
    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    private val _isLoading = MutableStateFlow(false)

    val uiState: StateFlow<SearchUiState> = combine(
        _query, _selectedFilter, _artists, _songs, _albums, _playlists, _isLoading
    ) { flows: Array<Any> ->
        SearchUiState(
            query = flows[0] as String,
            selectedFilter = flows[1] as SearchFilter,
            artists = flows[2] as List<Artist>,
            songs = flows[3] as List<com.taher.beatly.model.Song>,
            albums = flows[4] as List<Album>,
            playlists = flows[5] as List<Playlist>,
            isLoading = flows[6] as Boolean,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SearchUiState())

    init {
        observeSearch()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun observeSearch() {
        combine(_query.debounce(250.milliseconds), _selectedFilter) { query, filter ->
            query to filter
        }
            .distinctUntilChanged()
            .onEach { (query, _) ->
                if (query.isNotBlank()) _isLoading.value = true
            }
            .mapLatest { (query, filter) ->
                if (query.isBlank()) return@mapLatest null
                searchUseCase(query, filter)
            }
            .onEach { result ->
                _isLoading.value = false
                if (result == null) {
                    clearResults()
                    return@onEach
                }

                if (result is BeatlyResult.Success) {
                    val data = result.data
                    if (data.isNotEmpty()) {
                        processSearchResults(data)
                    } else {
                        clearResults()
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun processSearchResults(data: List<Any>) {
        when (data.first()) {
            is com.taher.beatly.domain.model.Artist -> {
                _artists.value = (data as List<com.taher.beatly.domain.model.Artist>).map { it.toUi() }
                _songs.value = emptyList()
                _domainSongs.value = emptyList()
                _albums.value = emptyList()
                _playlists.value = emptyList()
            }
            is com.taher.beatly.domain.model.Song -> {
                val domainSongs = data as List<com.taher.beatly.domain.model.Song>
                _domainSongs.value = domainSongs
                _songs.value = domainSongs.map { it.toUi() }
                _artists.value = emptyList()
                _albums.value = emptyList()
                _playlists.value = emptyList()
            }
            is com.taher.beatly.domain.model.Album -> {
                _albums.value = (data as List<com.taher.beatly.domain.model.Album>).map { it.toUi() }
                _songs.value = emptyList()
                _domainSongs.value = emptyList()
                _artists.value = emptyList()
                _playlists.value = emptyList()
            }
            is com.taher.beatly.domain.model.Playlist -> {
                _playlists.value = (data as List<com.taher.beatly.domain.model.Playlist>).map { it.toUi() }
                _artists.value = emptyList()
                _songs.value = emptyList()
                _domainSongs.value = emptyList()
                _albums.value = emptyList()
            }
        }
    }

    private fun clearResults() {
        _artists.value = emptyList()
        _songs.value = emptyList()
        _domainSongs.value = emptyList()
        _albums.value = emptyList()
        _playlists.value = emptyList()
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun onFilterSelected(filter: SearchFilter) {
        _selectedFilter.value = filter
    }

    fun onFollowToggled(artistId: String) {
        viewModelScope.launch { musicRepository.toggleFollowArtist(artistId) }
    }

    fun onPlaySong(songId: String) {
        viewModelScope.launch {
            val songs = _domainSongs.value
            val index = songs.indexOfFirst { it.id == songId }.coerceAtLeast(0)
            if (songs.isNotEmpty()) {
                playerRepository.playQueue(songs, index)
            }
        }
    }

    fun onLikeToggled(songId: String) {
        viewModelScope.launch {
            toggleLikeUseCase(songId)
            _songs.update { list ->
                list.map { if (it.id == songId) it.copy(isLiked = !it.isLiked) else it }
            }
        }
    }
}
