package com.taher.beatly.domain.model

data class User(
    val id        : String,
    val name      : String,
    val email     : String,
    val username  : String  = "",
    val avatarUrl : String  = "",
    val isPremium : Boolean = false
)

data class Song(
    val id           : String,
    val title        : String,
    val artistName   : String,
    val artistId     : String = "",
    val albumName    : String = "",
    val imageUrl     : String = "",
    val previewUrl   : String = "",   // Spotify 30-sec preview
    val durationMs   : Long   = 0L,
    val isLiked      : Boolean = false,
    val isInPlaylist : Boolean = false,
    val isSaved      : Boolean = false
)

data class Artist(
    val id               : String,
    val name             : String,
    val imageUrl         : String       = "",
    val monthlyListeners : Long         = 0L,
    val isFollowing      : Boolean      = false,
    val isVerified       : Boolean      = true,
    val genres           : List<String> = emptyList()
)

data class Genre(
    val id       : String,
    val name     : String,
    val imageUrl : String = ""
)

data class Playlist(
    val id          : String,
    val name        : String,
    val imageUrl    : String     = "",
    val songCount   : Int        = 0,
    val artistCount : Int        = 0,
    val ownerId     : String     = "",
    val songs       : List<Song> = emptyList()
)

data class Album(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val artistName: String = "",
    val totalTracks: Int = 0
)

sealed class BeatlyResult<out T> {
    data class Success<T>(val data: T)                              : BeatlyResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : BeatlyResult<Nothing>()
    data object Loading                                             : BeatlyResult<Nothing>()
}