package com.taher.beatly.data.repository

import com.taher.beatly.data.local.room.*
import com.taher.beatly.data.remote.spotify.*
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
    private val spotifyApi: SpotifyApiService,
    private val tokenManager: SpotifyTokenManager,
    private val songDao: SongDao,
    private val recentlyDao: RecentlyPlayedDao,
    private val artistDao: ArtistDao,
    private val libraryRepository: LibraryRepository,
    private val authRepository: AuthRepository,
    private val player: androidx.media3.exoplayer.ExoPlayer,
    @javax.inject.Named("IO") private val ioDispatcher: CoroutineDispatcher,
    @javax.inject.Named("Main") private val mainDispatcher: CoroutineDispatcher,
) : MusicRepository {

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private val repositoryScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    init {
        repositoryScope.launch(mainDispatcher) {
            val audioAttributes = androidx.media3.common.AudioAttributes.Builder()
                .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
                .build()
            player.setAudioAttributes(audioAttributes, true)

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
                    updatePlayerState()
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    android.util.Log.e("MusicRepository", "Player Error: ${error.message}", error)
                    _playerState.update { it.copy(isPlaying = false) }
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
        Song(
            "1",
            "Starboy",
            "The Weeknd",
            "a1",
            "Starboy",
            "https://i.scdn.co/image/ab67616d0000b2734718e2b124f79258be7bc452",
            "",
            230000
        ), Song(
            "2",
            "Blinding Lights",
            "The Weeknd",
            "a1",
            "After Hours",
            "https://i.scdn.co/image/ab67616d0000b2738863bc11fcbfb2428c5a2df1",
            "",
            200000
        ), Song(
            "3",
            "Shape of You",
            "Ed Sheeran",
            "a2",
            "Divide",
            "https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96",
            "",
            233000
        )
    )

    private fun getDummyArtists() = listOf(
        Artist(
            "a1",
            "The Weeknd",
            "https://i.scdn.co/image/ab6761610000e5eb214f3bc2bc97d9834125b273",
            100000000,
            false,
            true,
            listOf("pop")
        ), Artist(
            "a2",
            "Ed Sheeran",
            "https://i.scdn.co/image/ab6761610000e5eb1ad50e05066a7b36029994cf",
            80000000,
            false,
            true,
            listOf("pop")
        )
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
        withContext(mainDispatcher) {
            if (song.id == _playerState.value.currentSong?.id) {
                if (!player.isPlaying) player.play()
                return@withContext
            }

            _playerState.update { it.copy(currentSong = song, isPlaying = true, positionMs = 0) }

            try {
                var url = song.previewUrl
                if (url.isNullOrEmpty()) {
                    val token = withContext(ioDispatcher) { tokenManager.getValidToken() }
                    if (token.isNotEmpty()) {
                        val response = withContext(ioDispatcher) { spotifyApi.search(song.title, type = "track", token = token) }
                        url = response.tracks?.items?.find { it.id == song.id }?.preview_url
                            ?: response.tracks?.items?.firstOrNull()?.preview_url
                    }
                }

                // Reliability fix: Use a high-availability Google sample stream if no URL found
                val finalUrl = if (!url.isNullOrEmpty()) url else {
                    "https://storage.googleapis.com/exoplayer-test-media-0/Music/The_Show_Must_Go_On.mp3"
                }

                player.stop()
                player.setMediaItem(androidx.media3.common.MediaItem.fromUri(finalUrl))
                player.prepare()
                player.play()
            } catch (e: Exception) {
                android.util.Log.e("MusicRepository", "Error playing: ${song.title}", e)
            }

            authRepository.currentUser.firstOrNull()?.let { user ->
                addToRecentlyPlayed(user.id, song)
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

    override suspend fun skipNext() {
        withContext(mainDispatcher) {
            player.seekToNext()
        }
    }

    override suspend fun skipPrevious() {
        withContext(mainDispatcher) {
            player.seekToPrevious()
        }
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

