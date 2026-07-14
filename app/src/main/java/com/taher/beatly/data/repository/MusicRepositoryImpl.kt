package com.taher.beatly.data.repository

import com.taher.beatly.data.local.room.*
import com.taher.beatly.data.remote.spotify.*
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.*
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.LibraryItemIcon
import com.taher.beatly.model.PlayerState
import com.taher.beatly.model.Song as UiSong
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context,
    private val spotifyApi: SpotifyApiService,
    private val tokenManager: SpotifyTokenManager,
    private val songDao: SongDao,
    private val recentlyDao: RecentlyPlayedDao,
    private val artistDao: ArtistDao,
    private val libraryRepository: LibraryRepository,
    private val authRepository: AuthRepository
) : MusicRepository {

    private val player = androidx.media3.exoplayer.ExoPlayer.Builder(context).build()

    private val _userName = MutableStateFlow("Mr. Aiden Smith")

    override fun getUserName(): Flow<String> = _userName

    // ── Trending songs (New Releases or Recommendations) ───────────────────
    override suspend fun getTrendingSongs(): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        if (token.isEmpty()) throw Exception("Invalid Spotify Token")

        // Try getting new releases first
        // val response = spotifyApi.getNewReleases(limit = 10, token = token)

        // If we want real playable tracks, we prefer recommendations
        val recResponse = spotifyApi.getRecommendations(limit = 10, token = token)
        val trendingSongs = recResponse.tracks.map { it.toDomain() }

        songDao.insertSongs(trendingSongs.map { it.toEntity() })
        BeatlyResult.Success(trendingSongs)
    } catch (e: Exception) {
        BeatlyResult.Success(getDummySongs())
    }

    // ── Top Artists (search popular genres) ────────────────────────────────
    override suspend fun getTopArtists(): BeatlyResult<List<Artist>> = try {
        val token = tokenManager.getValidToken()
        if (token.isEmpty()) throw Exception("Invalid Spotify Token")

        val response = spotifyApi.search(
            query = "genre:pop year:2024",
            type = "artist",
            limit = 10,
            token = token
        )
        val artists = response.artists?.items?.map { it.toDomain() } ?: emptyList()
        artistDao.insertArtists(artists.map { it.toEntity() })
        BeatlyResult.Success(artists)
    } catch (e: Exception) {
        BeatlyResult.Success(getDummyArtists())
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
        ),
        Song(
            "2",
            "Blinding Lights",
            "The Weeknd",
            "a1",
            "After Hours",
            "https://i.scdn.co/image/ab67616d0000b2738863bc11fcbfb2428c5a2df1",
            "",
            200000
        ),
        Song(
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
        ),
        Artist(
            "a2",
            "Ed Sheeran",
            "https://i.scdn.co/image/ab6761610000e5eb1ad50e05066a7b36029994cf",
            80000000,
            false,
            true,
            listOf("pop")
        )
    )

    // ── Recently Played (local Room) ───────────────────────────────────────
    override suspend fun getRecentlyPlayed(userId: String): BeatlyResult<List<Song>> = try {
        val entities = recentlyDao.getRecentlyPlayed()
        val songs = entities.map {
            Song(
                id = it.id, title = it.title, artistName = it.artistName,
                imageUrl = it.imageUrl, durationMs = it.durationMs,
                artistId = ""
            )
        }
        BeatlyResult.Success(songs)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override fun getRecentlyPlayedFlow(userId: String): Flow<List<Song>> {
        return recentlyDao.getRecentlyPlayedFlow().map { entities ->
            entities.map {
                Song(
                    id = it.id, title = it.title, artistName = it.artistName,
                    imageUrl = it.imageUrl, durationMs = it.durationMs,
                    artistId = ""
                )
            }
        }
    }

    // ── Artist detail ──────────────────────────────────────────────────────
    override suspend fun getArtistDetail(artistId: String): BeatlyResult<Artist> = try {
        val token = tokenManager.getValidToken()
        val artist = spotifyApi.getArtist(artistId, token)
        BeatlyResult.Success(artist.toDomain())
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    // ── Artist top tracks ──────────────────────────────────────────────────
    override suspend fun getArtistTopTracks(artistId: String): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        val tracks = spotifyApi.getArtistTopTracks(artistId, token = token)
        BeatlyResult.Success(tracks.tracks.map { it.toDomain() })
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    // ── Search ─────────────────────────────────────────────────────────────
    override suspend fun searchArtists(query: String): BeatlyResult<List<Artist>> = try {
        val token = tokenManager.getValidToken()
        val result = spotifyApi.search(query, type = "artist", token = token)
        BeatlyResult.Success(result.artists?.items?.map { it.toDomain() } ?: emptyList())
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun searchSongs(query: String): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        val result = spotifyApi.search(query, type = "track", token = token)
        BeatlyResult.Success(result.tracks?.items?.map { it.toDomain() } ?: emptyList())
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun searchAlbums(query: String): BeatlyResult<List<Album>> = try {
        val token = tokenManager.getValidToken()
        val result = spotifyApi.search(query, type = "album", token = token)
        BeatlyResult.Success(result.albums?.items?.map { it.toDomain() } ?: emptyList())
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun searchPlaylists(query: String): BeatlyResult<List<Playlist>> = try {
        val token = tokenManager.getValidToken()
        val result = spotifyApi.search(query, type = "playlist", token = token)
        BeatlyResult.Success(result.playlists?.items?.map { it.toDomain() } ?: emptyList())
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun getGenreTracks(genreName: String): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        val response = spotifyApi.getRecommendations(genres = genreName, token = token)
        BeatlyResult.Success(response.tracks.map { it.toDomain() })
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun getPlaylistTracks(playlistId: String): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        val response = spotifyApi.getPlaylistTracks(playlistId, token)
        BeatlyResult.Success(response.items.map { it.track.toDomain() })
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun getAlbumTracks(albumId: String): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        val response = spotifyApi.getAlbumTracks(albumId, token)
        // Note: Album tracks response might not have full album object in items, 
        // we might need to fetch album details or handle missing fields.
        // Assuming SpotifyTrackItem is compatible enough.
        BeatlyResult.Success(response.items.map { it.toDomain() })
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    // ── Genres (hardcoded since Spotify deprecated the endpoint) ──────────
    override suspend fun getGenres(): BeatlyResult<List<Genre>> =
        BeatlyResult.Success(
            listOf(
                Genre("latin", "Latin"), Genre("pop", "Pop"), Genre("jazz", "Jazz"),
                Genre("classical", "Classical"), Genre("minimal", "Minimal"),
                Genre("indie", "Indie"), Genre("rock", "Rock"),
                Genre("hip-hop", "Hip Hop"), Genre("romance", "Romance")
            )
        )

    // ── Add to recently played ─────────────────────────────────────────────
    override suspend fun addToRecentlyPlayed(
        userId: String,
        song: UiSong
    ): BeatlyResult<Unit> = try {
        recentlyDao.insert(
            RecentlyPlayedEntity(
                id = song.id, title = song.title, artistName = song.artistName,
                imageUrl = song.imageUrl!!, durationMs = song.durationMs
            )
        )
        recentlyDao.trimOld()
        BeatlyResult.Success(Unit)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    override suspend fun toggleFollowArtist(artistId: String): BeatlyResult<Boolean> = try {
        val user =
            authRepository.currentUser.firstOrNull() ?: return BeatlyResult.Error("Not logged in")
        val token = tokenManager.getValidToken()
        val artist = spotifyApi.getArtist(artistId, token).toDomain()
        libraryRepository.toggleFollowArtist(user.id, artist)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private val repositoryScope =
        kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Main)

    init {
        simulatePlayback()
    }

    private fun simulatePlayback() {
        repositoryScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _playerState.update {
                    it.copy(
                        isPlaying = player.isPlaying,
                        positionMs = player.currentPosition,
                        durationMs = if (player.duration > 0) player.duration else it.durationMs
                    )
                }
            }
        }
    }

    // ── Library implementation ─────────────────────────────────────────────
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun getLibraryItems(): Flow<List<LibraryItem>> {
        val userFlow = authRepository.currentUser
        return userFlow.flatMapLatest { user ->
            if (user == null) return@flatMapLatest flowOf(emptyList<LibraryItem>())

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

            combine(playlistsFlow, flowOf(user)) { playlists, _ ->
                val virtualItems = listOf(
                    LibraryItem(
                        id = "liked_songs",
                        name = "Liked Songs",
                        songCount = 0, // could be dynamic
                        artistCount = 0,
                        icon = LibraryItemIcon.LIKED_SONGS
                    ),
                    LibraryItem(
                        id = "followed_artists",
                        name = "Followed Artists",
                        songCount = 0,
                        artistCount = 0,
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
                            id = ds.id, title = ds.title, artistName = ds.artistName,
                            artistId = ds.artistId, imageUrl = ds.imageUrl,
                            durationMs = ds.durationMs
                        )
                    }
                } else emptyList()
            }
        }
    }

    override suspend fun toggleLikeSong(songId: String) {
        val user = authRepository.currentUser.firstOrNull() ?: return
        val songEntity = songDao.getSongById(songId) ?: return
        val domainSong = Song(
            id = songEntity.id, title = songEntity.title, artistName = songEntity.artistName,
            artistId = songEntity.artistId, albumName = songEntity.albumName,
            imageUrl = songEntity.imageUrl, previewUrl = songEntity.previewUrl,
            durationMs = songEntity.durationMs, isLiked = !songEntity.isLiked
        )

        // Update local Room
        songDao.setLiked(songId, !songEntity.isLiked)

        // Update Firestore
        libraryRepository.toggleLikeSong(user.id, domainSong)
    }

    override suspend fun createLibraryPlaylist(name: String) {
        val user = authRepository.currentUser.firstOrNull() ?: return
        libraryRepository.createPlaylist(user.id, name)
    }

    // ── Player implementation ──────────────────────────────────────────────
    // ── Player implementation ──────────────────────────────────────────────
    override suspend fun playSong(song: UiSong) {
        if (song.id == _playerState.value.currentSong?.id) {
            if (!player.isPlaying) player.play()
            return
        }

        // تحديث حالة المشغل فوراً في الواجهة
        _playerState.update { it.copy(currentSong = song, isPlaying = true, positionMs = 0) }

        try {
            // الحصول على Token صالح
            val token = tokenManager.getValidToken()
            if (token.isEmpty()) {
                android.util.Log.e(
                    "MusicRepository",
                    "فشل الحصول على Spotify Token. تأكد من إعدادات Client ID و Secret."
                )
                return
            }

            // البحث عن المسار للحصول على رابط المعاينة (preview_url)
            val response = spotifyApi.search(song.title, type = "track", token = token)
            val foundTrack = response.tracks?.items?.find { it.id == song.id }
                ?: response.tracks?.items?.firstOrNull()

            val url = foundTrack?.preview_url ?: ""
            if (url.isNotEmpty()) {
                player.setMediaItem(androidx.media3.common.MediaItem.fromUri(url))
                player.prepare()
                player.play()
            } else {
                android.util.Log.w(
                    "MusicRepository",
                    "لم يتم العثور على رابط معاينة (preview_url) لهذه الأغنية: ${song.title}"
                )
            }
        } catch (e: Exception) {
            // معالجة الخطأ HTTP 400 أو أي خطأ شبكة آخر ومنع انهيار التطبيق
            android.util.Log.e(
                "MusicRepository",
                "خطأ أثناء محاولة تشغيل الأغنية: ${song.title}",
                e
            )
        }

        // إضافة الأغنية إلى قائمة "المشغلة مؤخراً"
        val user = authRepository.currentUser.firstOrNull()
        if (user != null) {
            addToRecentlyPlayed(user.id, song)
        }
    }

    override suspend fun togglePlayPause() {
        if (player.isPlaying) player.pause() else player.play()
    }

    override suspend fun seekTo(positionMs: Long) {
        player.seekTo(positionMs)
    }

    override suspend fun skipNext() {
        player.seekToNext()
    }

    override suspend fun skipPrevious() {
        player.seekToPrevious()
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
    id = id, title = title, artistName = artistName, artistId = artistId,
    albumName = albumName, imageUrl = imageUrl, previewUrl = previewUrl, durationMs = durationMs
)

fun Artist.toEntity() = ArtistEntity(
    id = id,
    name = name,
    imageUrl = imageUrl,
    monthlyListeners = monthlyListeners,
    isFollowing = isFollowing
)

fun SongEntity.toUiModel() = UiSong(
    id = id, title = title, artistName = artistName, artistId = artistId,
    imageUrl = imageUrl, durationMs = durationMs
)
