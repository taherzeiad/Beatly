package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.*
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.Song as UiSong
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun getUserName(): Flow<String>
    suspend fun getTrendingSongs(): BeatlyResult<List<Song>>
    suspend fun getTopArtists(): BeatlyResult<List<Artist>>
    suspend fun getRecentlyPlayed(userId: String): BeatlyResult<List<Song>>
    suspend fun getArtistDetail(artistId: String): BeatlyResult<Artist>
    suspend fun getArtistTopTracks(artistId: String): BeatlyResult<List<Song>>
    suspend fun searchArtists(query: String): BeatlyResult<List<Artist>>
    suspend fun searchSongs(query: String): BeatlyResult<List<Song>>
    suspend fun getGenres(): BeatlyResult<List<Genre>>
    suspend fun addToRecentlyPlayed(userId: String, song: UiSong): BeatlyResult<Unit>
    suspend fun toggleFollowArtist(artistId: String): BeatlyResult<Boolean>
    
    // Library methods
    fun getLibraryItems(): Flow<List<LibraryItem>>
    fun getLikedSongs(): Flow<List<UiSong>>
    suspend fun toggleLikeSong(songId: String)
    suspend fun createLibraryPlaylist(name: String)
    
    // Player methods
    suspend fun playSong(song: UiSong)
    suspend fun togglePlayPause()
    suspend fun seekTo(positionMs: Long)
    suspend fun skipNext()
    suspend fun skipPrevious()
}
