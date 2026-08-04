package com.taher.beatly.data.repository

import android.app.Application
import android.content.Intent
import com.taher.beatly.data.local.room.*
import com.taher.beatly.data.remote.spotify.*
import com.taher.beatly.data.service.PlaybackService
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.*
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.LibraryItemIcon
import com.taher.beatly.model.PlayerState
import com.taher.beatly.model.Song as UiSong
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val app: Application,
    private val spotifyApi: SpotifyApiService,
    private val tokenManager: SpotifyTokenManager,
    private val songDao: SongDao,
    private val recentlyDao: RecentlyPlayedDao,
    private val artistDao: ArtistDao,
    private val artistPlayCountDao: ArtistPlayCountDao,
    private val libraryRepository: LibraryRepository,
    private val authRepository: AuthRepository,
    private val player: androidx.media3.exoplayer.ExoPlayer,
    @javax.inject.Named("IO") private val ioDispatcher: CoroutineDispatcher,
    @javax.inject.Named("Main") private val mainDispatcher: CoroutineDispatcher,
) : MusicRepository {

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private var currentQueue: List<UiSong> = emptyList()

    private val repositoryScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    init {
        repositoryScope.launch(mainDispatcher) {
            player.addListener(object : androidx.media3.common.Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayerState()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updatePlayerState()
                    if (playbackState == androidx.media3.common.Player.STATE_READY) {
                        updatePlayerState() // Ensure duration is updated when ready
                    }
                }

                override fun onMediaItemTransition(
                    mediaItem: androidx.media3.common.MediaItem?,
                    reason: Int
                ) {
                    val currentSongId = mediaItem?.mediaId
                    val song = currentQueue.find { it.id == currentSongId }
                    _playerState.update { it.copy(currentSong = song) }
                    updatePlayerState()
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    val detailedError = "${error.errorCodeName}: ${error.localizedMessage} (Cause: ${error.cause?.message})"
                    android.util.Log.e("MusicRepository", "Player Error: $detailedError", error)
                    _playerState.update { it.copy(isPlaying = false, error = detailedError) }
                }
            })
        }
        simulatePlayback()
    }

    private fun updatePlayerState() {
        _playerState.update {
            it.copy(
                isPlaying = player.isPlaying,
                positionMs = player.currentPosition,
                durationMs = if (player.duration > 0) player.duration else it.durationMs
            )
        }
    }

    private val _userName = MutableStateFlow("Mr. Aiden Smith")

    override fun getUserName(): Flow<String> = _userName

    override fun getUserTopArtists(): Flow<List<Artist>> {
        return artistPlayCountDao.getTopArtistsFlow().map { entities ->
            entities.map {
                Artist(
                    id = it.artistId,
                    name = it.name,
                    imageUrl = it.imageUrl,
                    monthlyListeners = 0,
                    isVerified = false,
                    isFollowing = false
                )
            }
        }
    }

    // ── Trending songs (New Releases or Recommendations) ───────────────────
    override suspend fun getTrendingSongs(): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            if (token.isEmpty()) throw Exception("Invalid Spotify Token")

            // If we want real playable tracks, we prefer recommendations
            val recResponse =
                spotifyApi.getRecommendations(genres = "pop,hip-hop", limit = 10, token = token)
            val trendingSongs = recResponse.tracks.map { it.toDomain() }

            songDao.insertSongs(trendingSongs.map { it.toEntity() })
            BeatlyResult.Success(trendingSongs)
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Error getting trending songs", e)
            BeatlyResult.Success(getDummySongs())
        }
    }

    // ── Top Artists (search popular genres) ────────────────────────────────
    override suspend fun getTopArtists(): BeatlyResult<List<Artist>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            if (token.isEmpty()) throw Exception("Invalid Spotify Token")

            val response = spotifyApi.search(
                query = "genre:pop year:2024", type = "artist", limit = 10, token = token
            )
            val artists = response.artists?.items?.map { it.toDomain() } ?: emptyList()
            artistDao.insertArtists(artists.map { it.toEntity() })
            BeatlyResult.Success(artists)
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Error getting top artists", e)
            BeatlyResult.Success(getDummyArtists())
        }
    }

    private fun getDummySongs() = listOf(
        Song("1", "Starboy", "The Weeknd", "a1", "Starboy", "https://i.scdn.co/image/ab67616d0000b2734718e2b124f79258be7bc452", "", 230000),
        Song("2", "Blinding Lights", "The Weeknd", "a1", "After Hours", "https://i.scdn.co/image/ab67616d0000b2738863bc11fcbfb2428c5a2df1", "", 200000),
        Song("3", "Shape of You", "Ed Sheeran", "a2", "Divide", "https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96", "", 233000),
        Song("4", "Perfect", "Ed Sheeran", "a2", "Divide", "https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96", "", 263000),
        Song("5", "Levitating", "Dua Lipa", "a3", "Future Nostalgia", "https://i.scdn.co/image/ab67616d0000b273bd0e199f7d23f7936a77519b", "", 203000),
        Song("6", "Don't Start Now", "Dua Lipa", "a3", "Future Nostalgia", "https://i.scdn.co/image/ab67616d0000b273bd0e199f7d23f7936a77519b", "", 183000),
        Song("7", "Peaches", "Justin Bieber", "a4", "Justice", "https://i.scdn.co/image/ab67616d0000b273e6f9e3d77e497a7a58a6988d", "", 198000),
        Song("8", "STAY", "The Kid LAROI", "a5", "F*CK LOVE 3", "https://i.scdn.co/image/ab67616d0000b2734125b2734125b2734125b273", "", 141000),
        Song("9", "Save Your Tears", "The Weeknd", "a1", "After Hours", "https://i.scdn.co/image/ab67616d0000b2738863bc11fcbfb2428c5a2df1", "", 215000),
        Song("10", "As It Was", "Harry Styles", "a6", "Harry's House", "https://i.scdn.co/image/ab67616d0000b27311fcbfb2428c5a2df18863bc", "", 167000)
    )

    private fun getDummyArtists() = listOf(
        Artist("a1", "The Weeknd", "https://i.scdn.co/image/ab6761610000e5eb214f3bc2bc97d9834125b273", 100000000, false, true, listOf("pop")),
        Artist("a2", "Ed Sheeran", "https://i.scdn.co/image/ab6761610000e5eb1ad50e05066a7b36029994cf", 80000000, false, true, listOf("pop")),
        Artist("a3", "Dua Lipa", "https://i.scdn.co/image/ab6761610000e5ebbd0e199f7d23f7936a77519b", 70000000, false, true, listOf("pop")),
        Artist("a4", "Justin Bieber", "https://i.scdn.co/image/ab6761610000e5ebe6f9e3d77e497a7a58a6988d", 85000000, false, true, listOf("pop")),
        Artist("a5", "The Kid LAROI", "https://i.scdn.co/image/ab6761610000e5eb4125b2734125b2734125b273", 40000000, false, true, listOf("hip-hop")),
        Artist("a6", "Harry Styles", "https://i.scdn.co/image/ab6761610000e5eb11fcbfb2428c5a2df18863bc", 65000000, false, true, listOf("pop")),
        Artist("a7", "Billie Eilish", "https://i.scdn.co/image/ab6761610000e5eb214f3bc2bc97d9834125b273", 60000000, false, true, listOf("pop")),
        Artist("a8", "Drake", "https://i.scdn.co/image/ab6761610000e5eb1ad50e05066a7b36029994cf", 90000000, false, true, listOf("hip-hop")),
        Artist("a9", "Taylor Swift", "https://i.scdn.co/image/ab6761610000e5ebbd0e199f7d23f7936a77519b", 95000000, false, true, listOf("pop")),
        Artist("a10", "Ariana Grande", "https://i.scdn.co/image/ab6761610000e5ebe6f9e3d77e497a7a58a6988d", 75000000, false, true, listOf("pop"))
    )

    override suspend fun getRecentlyPlayed(userId: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val entities = recentlyDao.getRecentlyPlayed()
            val songs = entities.map {
                Song(
                    id = it.id,
                    title = it.title,
                    artistName = it.artistName,
                    imageUrl = it.imageUrl,
                    durationMs = it.durationMs,
                    artistId = "",
                )
            }
            BeatlyResult.Success(songs)
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override fun getRecentlyPlayedFlow(userId: String): Flow<List<Song>> {
        return recentlyDao.getRecentlyPlayedFlow().map { entities ->
            entities.map {
                Song(
                    id = it.id,
                    title = it.title,
                    artistName = it.artistName,
                    imageUrl = it.imageUrl,
                    durationMs = it.durationMs,
                    artistId = "",
                )
            }
        }
    }

    // ── Artist detail ──────────────────────────────────────────────────────
    override suspend fun getArtistDetail(artistId: String): BeatlyResult<Artist> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val artist = spotifyApi.getArtist(artistId, token)
            BeatlyResult.Success(artist.toDomain())
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    // ── Artist top tracks ──────────────────────────────────────────────────
    override suspend fun getArtistTopTracks(artistId: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val response = spotifyApi.getArtistTopTracks(artistId, market = "US", token = token)
            BeatlyResult.Success(response.tracks.map { it.toDomain() })
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    // ── Search ─────────────────────────────────────────────────────────────
    override suspend fun searchArtists(query: String): BeatlyResult<List<Artist>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val result = spotifyApi.search(query, type = "artist", token = token)
            BeatlyResult.Success(result.artists?.items?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun searchSongs(query: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val result = spotifyApi.search(query, type = "track", token = token)
            BeatlyResult.Success(result.tracks?.items?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun searchAlbums(query: String): BeatlyResult<List<Album>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val result = spotifyApi.search(query, type = "album", token = token)
            BeatlyResult.Success(result.albums?.items?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun searchPlaylists(query: String): BeatlyResult<List<Playlist>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val result = spotifyApi.search(query, type = "playlist", token = token)
            BeatlyResult.Success(result.playlists?.items?.map { it.toDomain() } ?: emptyList())
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun getGenreTracks(genreName: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val response = spotifyApi.getRecommendations(genres = genreName, token = token)
            BeatlyResult.Success(response.tracks.map { it.toDomain() })
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun getPlaylistTracks(playlistId: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val response = spotifyApi.getPlaylistTracks(playlistId, token)
            BeatlyResult.Success(response.items.map { it.track.toDomain() })
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun getAlbumTracks(albumId: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val response = spotifyApi.getAlbumTracks(albumId, token)
            // Note: Album tracks response might not have full album object in items, 
            // we might need to fetch album details or handle missing fields.
            // Assuming SpotifyTrackItem is compatible enough.
            BeatlyResult.Success(response.items.map { it.toDomain() })
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    // ── Genres (hardcoded since Spotify deprecated the endpoint) ──────────
    override suspend fun getGenres(): BeatlyResult<List<Genre>> = BeatlyResult.Success(
        listOf(
            Genre("latin", "Latin"),
            Genre("pop", "Pop"),
            Genre("jazz", "Jazz"),
            Genre("classical", "Classical"),
            Genre("minimal", "Minimal"),
            Genre("indie", "Indie"),
            Genre("rock", "Rock"),
            Genre("hip-hop", "Hip Hop"),
            Genre("romance", "Romance")
        )
    )

    override suspend fun addToRecentlyPlayed(
        userId: String, song: UiSong
    ): BeatlyResult<Unit> = withContext(ioDispatcher) {
        try {
            recentlyDao.insert(
                RecentlyPlayedEntity(
                    id = song.id,
                    title = song.title,
                    artistName = song.artistName,
                    artistId = song.artistId,
                    imageUrl = song.imageUrl ?: "",
                    durationMs = song.durationMs,
                )
            )
            recentlyDao.trimOld()

            // Update artist play count
            if (song.artistId.isNotEmpty()) {
                artistPlayCountDao.insert(
                    ArtistPlayCountEntity(
                        artistId = song.artistId,
                        name = song.artistName,
                        imageUrl = song.imageUrl ?: ""
                    )
                )
                artistPlayCountDao.incrementPlayCount(song.artistId)
            }

            BeatlyResult.Success(Unit)
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun toggleFollowArtist(artistId: String): BeatlyResult<Boolean> = withContext(ioDispatcher) {
        try {
            val user =
                authRepository.currentUser.firstOrNull() ?: return@withContext BeatlyResult.Error("Not logged in")
            val token = tokenManager.getValidToken()
            val artist = spotifyApi.getArtist(artistId, token).toDomain()
            libraryRepository.toggleFollowArtist(user.id, artist)
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }


    private fun simulatePlayback() {
        repositoryScope.launch {
            while (true) {
                delay(1000)
                if (player.isPlaying) {
                    updatePlayerState()
                }
            }
        }
    }

    // ── Library implementation ─────────────────────────────────────────────
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getLibraryItems(): Flow<List<LibraryItem>> {
        val userFlow = authRepository.currentUser
        return userFlow.flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(emptyList())

            val playlistsFlow = libraryRepository.getLibrary(user.id).map { result ->
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

            val likedSongsFlow = libraryRepository.getLikedSongsFlow(user.id)
            val followedArtistsFlow = libraryRepository.getFollowedArtistsFlow(user.id)

            combine(
                playlistsFlow, likedSongsFlow, followedArtistsFlow
            ) { playlists, _, _ ->
                val virtualItems = listOf(
                    LibraryItem(
                        id = "liked_songs",
                        name = "Liked songs",
                        songCount = 100,
                        artistCount = 24,
                        icon = LibraryItemIcon.LIKED_SONGS
                    ), LibraryItem(
                        id = "followed_artists",
                        name = "Artist you follow",
                        songCount = 100,
                        artistCount = 12,
                        icon = LibraryItemIcon.FOLLOWED_ARTISTS
                    )
                )
                virtualItems + playlists
            }
        }
    }

    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getLikedSongs(): Flow<List<UiSong>> {
        val userFlow = authRepository.currentUser
        return userFlow.flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(emptyList<UiSong>())
            libraryRepository.getLikedSongsFlow(user.id).map { result ->
                if (result is BeatlyResult.Success) {
                    result.data.map { ds ->
                        UiSong(
                            id = ds.id,
                            title = ds.title,
                            artistName = ds.artistName,
                            artistId = ds.artistId,
                            imageUrl = ds.imageUrl,
                            previewUrl = ds.previewUrl,
                            durationMs = ds.durationMs
                        )
                    }
                } else emptyList()
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
            libraryRepository.toggleLikeSong(user.id, domainSong)
        }
    }

    override suspend fun createLibraryPlaylist(name: String) {
        withContext(ioDispatcher) {
            val user = authRepository.currentUser.firstOrNull() ?: return@withContext
            libraryRepository.createPlaylist(user.id, name)
        }
    }

    // ── Player implementation ──────────────────────────────────────────────
    override suspend fun playSong(song: UiSong) {
        playQueue(listOf(song), 0)
    }

    override suspend fun playQueue(songs: List<UiSong>, startIndex: Int) {
        withContext(mainDispatcher) {
            try {
                app.startService(Intent(app, PlaybackService::class.java))
                android.util.Log.d("MusicRepository", "PlaybackService started from Repository")
            } catch (e: Exception) {
                android.util.Log.e("MusicRepository", "Failed to start PlaybackService", e)
            }

            currentQueue = songs
            val currentSong = songs.getOrNull(startIndex)
            _playerState.update { it.copy(currentSong = currentSong, isPlaying = true, positionMs = 0, error = null) }

            player.stop()
            player.clearMediaItems()

            val mediaItems = songs.map { song ->
                val mediaMetadata = androidx.media3.common.MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artistName)
                    .setArtworkUri(android.net.Uri.parse(song.imageUrl ?: ""))
                    .build()

                val url = if (!song.previewUrl.isNullOrEmpty()) {
                    android.util.Log.d("MusicRepository", "Playing Spotify preview: ${song.previewUrl}")
                    song.previewUrl
                } else {
                    val fallback = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                    android.util.Log.w("MusicRepository", "No preview URL for ${song.title}, using verified fallback: $fallback")
                    fallback
                }

                androidx.media3.common.MediaItem.Builder()
                    .setMediaId(song.id)
                    .setUri(url)
                    .setMimeType("audio/mpeg")
                    .setMediaMetadata(mediaMetadata)
                    .build()
            }

            player.setMediaItems(mediaItems, startIndex, 0L)
            player.prepare()
            player.play()

            currentSong?.let { s ->
                authRepository.currentUser.firstOrNull()?.let { user ->
                    addToRecentlyPlayed(user.id, s)
                }
            }
        }
    }

    override suspend fun togglePlayPause() {
        withContext(mainDispatcher) {
            if (player.isPlaying) player.pause() else player.play()
        }
    }

    override suspend fun seekTo(positionMs: Long) {
        withContext(mainDispatcher) {
            player.seekTo(positionMs)
        }
    }

    override suspend fun seekForward() {
        withContext(mainDispatcher) {
            player.seekTo(player.currentPosition + 10_000)
        }
    }

    override suspend fun seekBackward() {
        withContext(mainDispatcher) {
            player.seekTo(kotlin.math.max(0, player.currentPosition - 10_000))
        }
    }

    override suspend fun skipNext() {
        withContext(mainDispatcher) {
            if (player.hasNextMediaItem()) {
                player.seekToNext()
            }
        }
    }

    override suspend fun skipPrevious() {
        withContext(mainDispatcher) {
            if (player.hasPreviousMediaItem()) {
                player.seekToPrevious()
            }
        }
    }

    override fun clearError() {
        _playerState.update { it.copy(error = null) }
    }
}

// ── Mappers ────────────────────────────────────────────────────────────────

fun SpotifyTrackItem.toDomain() = Song(
    id = id,
    title = name,
    artistName = artists.firstOrNull()?.name ?: "",
    artistId = artists.firstOrNull()?.id ?: "",
    albumName = album.name,
    imageUrl = album.images.firstOrNull()?.url ?: "",
    previewUrl = preview_url ?: "",
    durationMs = duration_ms
)

fun SpotifyArtist.toDomain() = Artist(
    id = id,
    name = name,
    imageUrl = images.firstOrNull()?.url ?: "",
    monthlyListeners = followers.total,
    genres = genres
)

fun SpotifyAlbumItem.toDomain() = Album(
    id = id,
    name = name,
    imageUrl = images.firstOrNull()?.url ?: "",
    artistName = artists.firstOrNull()?.name ?: "",
    totalTracks = total_tracks
)

fun SpotifyPlaylistItem.toDomain() = Playlist(
    id = id,
    name = name,
    imageUrl = images.firstOrNull()?.url ?: "",
    songCount = tracks.total,
    ownerId = owner.display_name
)

fun Song.toEntity() = SongEntity(
    id = id,
    title = title,
    artistName = artistName,
    artistId = artistId,
    albumName = albumName,
    imageUrl = imageUrl,
    previewUrl = previewUrl,
    durationMs = durationMs
)

fun Artist.toEntity() = ArtistEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    monthlyListeners = monthlyListeners,
    isFollowing = isFollowing
)

