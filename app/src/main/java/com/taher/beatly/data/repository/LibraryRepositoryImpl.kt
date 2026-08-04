package com.taher.beatly.data.repository

import com.taher.beatly.data.remote.firebase.FirestoreLibraryDataSource
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.LibraryRepository
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.LibraryItemIcon
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LibraryRepositoryImpl @Inject constructor(
    private val dataSource: FirestoreLibraryDataSource,
    private val authRepository: AuthRepository,
    private val songDao: com.taher.beatly.data.local.room.SongDao,
    @Named("IO") private val ioDispatcher: kotlinx.coroutines.CoroutineDispatcher
) : LibraryRepository {

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getLibraryItems(): Flow<List<LibraryItem>> {
        val userFlow = authRepository.currentUser
        return userFlow.flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(emptyList())

            val playlistsFlow = getLibrary(user.id).map { result ->
                if (result is BeatlyResult.Success) {
                    result.data.map { playlist ->
                        LibraryItem(
                            id = playlist.id,
                            name = playlist.name,
                            songCount = playlist.songCount,
                            artistCount = 0,
                            icon = LibraryItemIcon.PLAYLIST,
                            imageUrl = playlist.imageUrl,
                            isCustomPlaylist = true
                        )
                    }
                } else emptyList()
            }

            val likedSongsFlow = getLikedSongsFlow(user.id)
            val followedArtistsFlow = getFollowedArtistsFlow(user.id)

            combine(
                playlistsFlow, likedSongsFlow, followedArtistsFlow
            ) { playlists, likedSongsResult, followedArtistsResult ->
                val likedCount = (likedSongsResult as? BeatlyResult.Success)?.data?.size ?: 0
                val artistCount = (followedArtistsResult as? BeatlyResult.Success)?.data?.size ?: 0

                val virtualItems = listOf(
                    LibraryItem(
                        id = "liked_songs",
                        name = "Liked songs",
                        songCount = likedCount,
                        artistCount = 0,
                        icon = LibraryItemIcon.LIKED_SONGS
                    ), LibraryItem(
                        id = "followed_artists",
                        name = "Artist you follow",
                        songCount = 0,
                        artistCount = artistCount,
                        icon = LibraryItemIcon.FOLLOWED_ARTISTS
                    )
                )
                virtualItems + playlists
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getLikedSongs(): Flow<List<Song>> {
        val userFlow = authRepository.currentUser
        return userFlow.flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(emptyList())
            getLikedSongsFlow(user.id).map { result ->
                if (result is BeatlyResult.Success) result.data else emptyList()
            }
        }
    }

    override suspend fun toggleLikeSong(songId: String) {
        withContext(ioDispatcher) {
            val user = authRepository.currentUser.firstOrNull() ?: return@withContext
            val songEntity = songDao.getSongById(songId) ?: return@withContext
            val domainSong = Song(
                id = songEntity.id,
                title = songEntity.title,
                artistName = songEntity.artistName,
                artistId = songEntity.artistId,
                albumName = songEntity.albumName,
                imageUrl = songEntity.imageUrl,
                previewUrl = songEntity.previewUrl,
                durationMs = songEntity.durationMs,
                isLiked = !songEntity.isLiked
            )

            // Update local Room
            songDao.setLiked(songId, !songEntity.isLiked)

            // Update Firestore
            dataSource.toggleLikeSong(user.id, domainSong)
        }
    }

    override suspend fun createLibraryPlaylist(name: String) {
        withContext(ioDispatcher) {
            val user = authRepository.currentUser.firstOrNull() ?: return@withContext
            createPlaylist(user.id, name)
        }
    }

    override fun getLibrary(userId: String): Flow<BeatlyResult<List<Playlist>>> = 
        dataSource.getLibrary(userId)

    override fun getLikedSongsFlow(userId: String): Flow<BeatlyResult<List<Song>>> =
        dataSource.getLikedSongsFlow(userId)

    override fun getFollowedArtistsFlow(userId: String): Flow<BeatlyResult<List<Artist>>> =
        dataSource.getFollowedArtistsFlow(userId)

    override suspend fun createPlaylist(userId: String, name: String): BeatlyResult<Playlist> = 
        dataSource.createPlaylist(userId, name)

    suspend fun deletePlaylistInternal(playlistId: String, userId: String): BeatlyResult<Unit> =
        dataSource.deletePlaylist(playlistId, userId)

    override suspend fun deletePlaylist(playlistId: String): BeatlyResult<Unit> {
        val user = authRepository.currentUser.firstOrNull() ?: return BeatlyResult.Error("Not logged in")
        return dataSource.deletePlaylist(playlistId, user.id)
    }

    override suspend fun addSongToPlaylist(playlistId: String, song: Song): BeatlyResult<Unit> {
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
