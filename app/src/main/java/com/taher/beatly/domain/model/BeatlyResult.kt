package com.taher.beatly.domain.model

sealed class BeatlyResult<out T> {
    data class Success<T>(val data: T)                              : BeatlyResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : BeatlyResult<Nothing>()
    data object Loading                                             : BeatlyResult<Nothing>()
}
