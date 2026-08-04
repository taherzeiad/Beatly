package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.*
import com.taher.beatly.model.LibraryItem
import kotlinx.coroutines.flow.Flow

interface LibraryRepository {
    fun getLibraryItems(): Flow<List<LibraryItem>>
    fun getLikedSongs(): Flow<List<Song>>
    suspend fun toggleLikeSong(songId: String)
    suspend fun createLibraryPlaylist(name: String)

    // Lower level methods (Firestore)
    fun getLibrary(userId: String): Flow<BeatlyResult<List<Playlist>>>
    fun getLikedSongsFlow(userId: String): Flow<BeatlyResult<List<Song>>>
    fun getFollowedArtistsFlow(userId: String): Flow<BeatlyResult<List<Artist>>>
    suspend fun createPlaylist(userId: String, name: String): BeatlyResult<Playlist>
    suspend fun deletePlaylist(playlistId: String): BeatlyResult<Unit>
    suspend fun addSongToPlaylist(playlistId: String, song: Song): BeatlyResult<Unit>
    suspend fun removeSongFromPlaylist(playlistId: String, songId: String): BeatlyResult<Unit>
    suspend fun toggleLikeSong(userId: String, song: Song): BeatlyResult<Boolean>
    suspend fun getLikedSongs(userId: String): BeatlyResult<List<Song>>
    suspend fun toggleFollowArtist(userId: String, artist: Artist): BeatlyResult<Boolean>
    suspend fun getFollowedArtists(userId: String): BeatlyResult<List<Artist>>
}
