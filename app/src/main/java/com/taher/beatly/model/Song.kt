package com.taher.beatly.model

/**
 * Core domain models. Kept free of any Android / Compose imports so they
 * can be reused by the domain and data layers without pulling UI deps.
 */

data class Song(
    val id: String,
    val title: String,
    val artistName: String,
    val artistId: String,
    val imageUrl: String? = null,
    val durationMs: Long = 0L,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false
)

data class Artist(
    val id: String,
    val name: String,
    val imageUrl: String? = null,
    val isVerified: Boolean = false,
    val isFollowing: Boolean = false,
    val monthlyListeners: Long = 0L,
    val popularSongs: List<Song> = emptyList()
)

data class Genre(
    val id: String,
    val name: String,
    val imageUrl: String? = null
)

enum class LibraryItemIcon { LIKED_SONGS, FOLLOWED_ARTISTS, PLAYLIST, CUSTOM_IMAGE }

data class LibraryItem(
    val id: String,
    val name: String,
    val songCount: Int,
    val artistCount: Int,
    val icon: LibraryItemIcon,
    val imageUrl: String? = null,
    val isCustomPlaylist: Boolean = false
)

data class PlayerState(
    val currentSong: Song? = null,
    val featuring: String? = null,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L
)

enum class SearchFilter { TOP, SONGS, ARTISTS, ALBUMS, PLAYLISTS }

enum class LibraryFilter { SONGS, PLAYLIST, ALBUMS, ARTIST }