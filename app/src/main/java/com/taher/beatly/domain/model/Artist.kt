package com.taher.beatly.domain.model

data class Artist(
    val id               : String,
    val name             : String,
    val imageUrl         : String       = "",
    val monthlyListeners : Long         = 0L,
    val isFollowing      : Boolean      = false,
    val isVerified       : Boolean      = true,
    val genres           : List<String> = emptyList()
)
