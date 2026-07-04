package com.taher.beatly.data

import com.taher.beatly.model.Artist
import com.taher.beatly.model.Genre
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.Song
import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for music data, consumed by the ViewModels.
 * Swap [FakeMusicRepository] for a real implementation (Room + remote
 * API) without touching any UI or ViewModel code.
 */
interface MusicRepository {

    fun getUserName(): Flow<String>

    fun getTrendingSongs(): Flow<List<Song>>
    fun getTopArtists(): Flow<List<Artist>>
    fun getRecentlyPlayed(): Flow<List<Song>>

    fun getGenres(): Flow<List<Genre>>

    fun searchArtists(query: String): Flow<List<Artist>>
    suspend fun toggleFollowArtist(artistId: String)

    fun getArtistById(artistId: String): Flow<Artist?>

    fun getLibraryItems(): Flow<List<LibraryItem>>
    fun getLikedSongs(): Flow<List<Song>>
    suspend fun toggleLikeSong(songId: String)
    suspend fun createLibraryPlaylist(name: String)

    suspend fun playSong(song: Song)
    suspend fun togglePlayPause()
    suspend fun seekTo(positionMs: Long)
    suspend fun skipNext()
    suspend fun skipPrevious()
}