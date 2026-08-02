package com.taher.beatly.domain.model

data class User(
    val id        : String,
    val name      : String,
    val email     : String,
    val username  : String  = "",
    val avatarUrl : String  = "",
    val isPremium : Boolean = false,
    val birthDate : String  = "",
    val gender    : String  = ""
)
