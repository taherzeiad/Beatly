package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.PlayerState
import com.taher.beatly.domain.model.Song
import kotlinx.coroutines.flow.Flow

interface PlayerRepository {
    val playerState: Flow<PlayerState>
    suspend fun playSong(song: Song)
    suspend fun playQueue(songs: List<Song>, startIndex: Int)
    suspend fun togglePlayPause()
    suspend fun seekTo(positionMs: Long)
    suspend fun seekForward()
    suspend fun seekBackward()
    suspend fun skipNext()
    suspend fun skipPrevious()
    fun clearError()
}
