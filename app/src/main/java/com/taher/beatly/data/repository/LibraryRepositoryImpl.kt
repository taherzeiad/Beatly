package com.taher.beatly.data.repository

import com.taher.beatly.data.remote.firebase.FirestoreLibraryDataSource
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.LibraryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreLibraryDataSource,
    private val authRepository: AuthRepository
) : LibraryRepository {

    override fun getLibrary(userId: String): Flow<BeatlyResult<List<Playlist>>> = 
        dataSource.getLibrary(userId)

    override suspend fun createPlaylist(userId: String, name: String): BeatlyResult<Playlist> = 
        dataSource.createPlaylist(userId, name)

    override suspend fun deletePlaylist(playlistId: String): BeatlyResult<Unit> {
        val user = authRepository.currentUser.firstOrNull() ?: return BeatlyResult.Error("Not logged in")
        return dataSource.deletePlaylist(playlistId, user.id)
    }

    override suspend fun addSongToPlaylist(playlistId: String, song: Song): BeatlyResult<Unit> {
        // Implement Firestore logic to add song
        return BeatlyResult.Success(Unit)
    }

    override suspend fun removeSongFromPlaylist(playlistId: String, songId: String): BeatlyResult<Unit> {
        return BeatlyResult.Success(Unit)
    }

    override suspend fun toggleLikeSong(userId: String, song: Song): BeatlyResult<Boolean> = 
        dataSource.toggleLikeSong(userId, song)

    override suspend fun getLikedSongs(userId: String): BeatlyResult<List<Song>> = 
        dataSource.getLikedSongs(userId)

    override suspend fun toggleFollowArtist(userId: String, artist: Artist): BeatlyResult<Boolean> = 
        dataSource.toggleFollowArtist(userId, artist)

    override suspend fun getFollowedArtists(userId: String): BeatlyResult<List<Artist>> = 
        dataSource.getFollowedArtists(userId)
}
