package com.taher.beatly.domain.model

data class Album(
    val id: String,
    val name: String,
    val imageUrl: String = "",
    val artistName: String = "",
    val totalTracks: Int = 0
)
