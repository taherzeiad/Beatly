package com.taher.beatly.domain.model

data class Playlist(
    val id          : String,
    val name        : String,
    val imageUrl    : String     = "",
    val songCount   : Int        = 0,
    val artistCount : Int        = 0,
    val ownerId     : String     = "",
    val songs       : List<Song> = emptyList()
)
