package com.taher.beatly.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.Album
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Playlist
import com.taher.beatly.model.SearchFilter
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
            isLoading = flows[6] as Boolean
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

                when (filter) {
                    SearchFilter.ARTISTS -> repository.searchArtists(query)
                    SearchFilter.SONGS -> repository.searchSongs(query)
                    SearchFilter.ALBUMS -> repository.searchAlbums(query)
                    SearchFilter.PLAYLISTS -> repository.searchPlaylists(query)
                    else -> repository.searchArtists(query)
                }
            }
            .onEach { result ->
                _isLoading.value = false
                if (result == null) {
                    _artists.value = emptyList()
                    _songs.value = emptyList()
                    _albums.value = emptyList()
                    _playlists.value = emptyList()
                    return@onEach
                }

                if (result is BeatlyResult.Success) {
                    val data = result.data
                    if (data.isNotEmpty()) {
                        val first = data.first()
                        when (first) {
                            is com.taher.beatly.domain.model.Artist -> {
                                _artists.value =
                                    (data as List<com.taher.beatly.domain.model.Artist>).map { da ->
                                        Artist(
                                            id = da.id,
                                            name = da.name,
                                            imageUrl = da.imageUrl,
                                            isVerified = da.isVerified,
                                            isFollowing = da.isFollowing,
                                            monthlyListeners = da.monthlyListeners
                                        )
                                    }
                                _songs.value = emptyList()
                                _albums.value = emptyList()
                                _playlists.value = emptyList()
                            }

                            is com.taher.beatly.domain.model.Song -> {
                                _songs.value =
                                    (data as List<com.taher.beatly.domain.model.Song>).map { ds ->
                                        com.taher.beatly.model.Song(
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
                                _artists.value = emptyList()
                                _albums.value = emptyList()
                                _playlists.value = emptyList()
                            }

                            is com.taher.beatly.domain.model.Album -> {
                                _albums.value =
                                    (data as List<com.taher.beatly.domain.model.Album>).map { da ->
                                        Album(
                                            id = da.id, name = da.name, artistName = da.artistName,
                                            imageUrl = da.imageUrl, totalTracks = da.totalTracks
                                        )
                                    }
                                _artists.value = emptyList()
                                _songs.value = emptyList()
                                _playlists.value = emptyList()
                            }

                            is com.taher.beatly.domain.model.Playlist -> {
                                _playlists.value =
                                    (data as List<com.taher.beatly.domain.model.Playlist>).map { dp ->
                                        Playlist(
                                            id = dp.id, name = dp.name, ownerName = dp.ownerId,
                                            imageUrl = dp.imageUrl, songCount = dp.songCount
                                        )
                                    }
                                _artists.value = emptyList()
                                _songs.value = emptyList()
                                _albums.value = emptyList()
                            }
                        }
                    } else {
                        _artists.value = emptyList()
                        _songs.value = emptyList()
                        _albums.value = emptyList()
                        _playlists.value = emptyList()
                    }
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

    fun onPlaySong(song: com.taher.beatly.model.Song) {
        viewModelScope.launch { repository.playSong(song) }
    }

    fun onLikeToggled(songId: String) {
        viewModelScope.launch { repository.toggleLikeSong(songId) }
    }
}
