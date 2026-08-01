package com.taher.beatly.domain.model

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
