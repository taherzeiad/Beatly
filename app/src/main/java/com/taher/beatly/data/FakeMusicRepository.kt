package com.taher.beatly.data

import com.taher.beatly.model.Artist
import com.taher.beatly.model.Genre
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.LibraryItemIcon
import com.taher.beatly.model.Song
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

@Singleton
class FakeMusicRepository @Inject constructor() : MusicRepository {

    private val _userName = MutableStateFlow("Mr. Aiden Smith")

    private val _artists = MutableStateFlow(
        listOf(
            Artist("a1", "Justin Bieber", isVerified = true, isFollowing = false),
            Artist("a2", "Lady Gaga", isVerified = true, isFollowing = true),
            Artist(
                "a3", "Taylor Swift", isVerified = true, isFollowing = false,
                monthlyListeners = 204_232_143L,
                popularSongs = listOf(
                    Song("s10", "Love Story", "Taylor Swift", "a3", isLiked = true),
                    Song("s11", "Blank Space", "Taylor Swift", "a3", isLiked = true)
                )
            ),
            Artist("a4", "Weeknd", isVerified = true, isFollowing = true),
            Artist("a5", "Khalid", isVerified = true, isFollowing = false),
            Artist("a6", "Billie Eilish", isVerified = true, isFollowing = true),
            Artist("a7", "Katy Perry", isVerified = true, isFollowing = false),
            Artist("a8", "Paramore", isVerified = false, isFollowing = false)
        )
    )

    private val _trendingSongs = MutableStateFlow(
        listOf(
            Song("s1", "Sharks", "Imagine Dragons", "a9", isLiked = true),
            Song("s2", "God Is a Woman", "Ariana Grande", "a10", isLiked = true),
            Song("s3", "Handsome", "Warren Hue", "a11", isLiked = true)
        )
    )

    private val _recentlyPlayed = MutableStateFlow(
        listOf(Song("s4", "Ghost", "Justin Bieber", "a1", isLiked = true, isSaved = true))
    )

    private val _genres = MutableStateFlow(
        listOf(
            Genre("g1", "Latin"), Genre("g2", "Pop"),
            Genre("g3", "Jazz"), Genre("g4", "Classical"),
            Genre("g5", "Latin"), Genre("g6", "Minimal"),
            Genre("g7", "Indie"), Genre("g8", "Rock"),
            Genre("g9", "Hip Hop"), Genre("g10", "Romance")
        )
    )

    private val _likedSongs = MutableStateFlow(
        listOf(
            Song("s1", "Sharks", "Imagine Dragons", "a9", isLiked = true),
            Song("s2", "God Is a Woman", "Ariana Grande", "a10", isLiked = true),
            Song("s5", "Handsome", "Warren Hue", "a11", isLiked = true),
            Song("s6", "Work from Home", "Fifth Harmony", "a12", isLiked = true),
            Song("s7", "Gangsta's Paradise", "Coolio", "a13", isLiked = true)
        )
    )

    private val _libraryItems = MutableStateFlow(
        listOf(
            LibraryItem("l1", "Liked songs", 100, 24, LibraryItemIcon.LIKED_SONGS),
            LibraryItem("l2", "Artist you follow", 100, 12, LibraryItemIcon.FOLLOWED_ARTISTS),
            LibraryItem("l3", "Favorite Playlist", 100, 12, LibraryItemIcon.PLAYLIST),
            LibraryItem("l4", "Metallica", 50, 6, LibraryItemIcon.CUSTOM_IMAGE),
            LibraryItem("l5", "The Weeknd", 40, 1, LibraryItemIcon.CUSTOM_IMAGE)
        )
    )

    private val _playerSong = MutableStateFlow(
        Song("s8", "Free Spirit", "Khalid", "a5")
    )
    private val _isPlaying = MutableStateFlow(true)
    private val _positionMs = MutableStateFlow(215_000L)
    private val _durationMs = MutableStateFlow(230_000L)

    override fun getUserName(): StateFlow<String> = _userName
    override fun getTrendingSongs(): StateFlow<List<Song>> = _trendingSongs
    override fun getTopArtists(): StateFlow<List<Artist>> = _artists
    override fun getRecentlyPlayed(): StateFlow<List<Song>> = _recentlyPlayed
    override fun getGenres(): StateFlow<List<Genre>> = _genres

    override fun searchArtists(query: String) = _artists.map { list ->
        if (query.isBlank()) list else list.filter { it.name.contains(query, ignoreCase = true) }
    }

    override suspend fun toggleFollowArtist(artistId: String) {
        _artists.value = _artists.value.map {
            if (it.id == artistId) it.copy(isFollowing = !it.isFollowing) else it
        }
    }

    override fun getArtistById(artistId: String) = _artists.map { list ->
        list.firstOrNull { it.id == artistId }
    }

    override fun getLibraryItems(): StateFlow<List<LibraryItem>> = _libraryItems
    override fun getLikedSongs(): StateFlow<List<Song>> = _likedSongs

    override suspend fun toggleLikeSong(songId: String) {
        _likedSongs.value = _likedSongs.value.map {
            if (it.id == songId) it.copy(isLiked = !it.isLiked) else it
        }
        _trendingSongs.value = _trendingSongs.value.map {
            if (it.id == songId) it.copy(isLiked = !it.isLiked) else it
        }
    }

    override suspend fun createLibraryPlaylist(name: String) {
        if (name.isBlank()) return
        _libraryItems.value = _libraryItems.value + LibraryItem(
            id = "l_${System.currentTimeMillis()}",
            name = name,
            songCount = 0,
            artistCount = 0,
            icon = LibraryItemIcon.PLAYLIST,
            isCustomPlaylist = true
        )
    }

    override suspend fun playSong(song: Song) {
        _playerSong.value = song
        _isPlaying.value = true
        _positionMs.value = 0L
    }

    override suspend fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
    }

    override suspend fun seekTo(positionMs: Long) {
        _positionMs.value = positionMs.coerceIn(0L, _durationMs.value)
    }

    override suspend fun skipNext() { /* wire to real playback queue */ }
    override suspend fun skipPrevious() { /* wire to real playback queue */ }
}