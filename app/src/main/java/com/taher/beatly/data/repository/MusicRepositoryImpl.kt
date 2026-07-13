package com.taher.beatly.data.repository

import com.taher.beatly.data.local.room.*
import com.taher.beatly.data.remote.spotify.*
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.PlayerState
import com.taher.beatly.model.Song as UiSong
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApiService,
    private val tokenManager: SpotifyTokenManager,
    private val songDao: SongDao,
    private val recentlyDao: RecentlyPlayedDao,
    private val artistDao: ArtistDao,
) : MusicRepository {

    private val _userName = MutableStateFlow("Mr. Aiden Smith")

    override fun getUserName(): Flow<String> = _userName

    // ── Trending songs (Spotify recommendations + cache) ───────────────────
    override suspend fun getTrendingSongs(): BeatlyResult<List<Song>> = try {
        val token = tokenManager.getValidToken()
        if (token.isEmpty()) throw Exception("Invalid Spotify Token")
        
        val response = spotifyApi.getRecommendations(token = token)
        val songs = response.tracks.map { it.toDomain() }
        // Cache in Room
        songDao.insertSongs(songs.map { it.toEntity() })
        BeatlyResult.Success(songs)
    } catch (e: Exception) {
        // Fallback to dummy data if API fails (e.g. no credentials)
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
        Song("1", "Starboy", "The Weeknd", "a1", "Starboy", "https://i.scdn.co/image/ab67616d0000b2734718e2b124f79258be7bc452", "", 230000),
        Song("2", "Blinding Lights", "The Weeknd", "a1", "After Hours", "https://i.scdn.co/image/ab67616d0000b2738863bc11fcbfb2428c5a2df1", "", 200000),
        Song("3", "Shape of You", "Ed Sheeran", "a2", "Divide", "https://i.scdn.co/image/ab67616d0000b273ba5db46f4b838ef6027e6f96", "", 233000)
    )

    private fun getDummyArtists() = listOf(
        Artist("a1", "The Weeknd", "https://i.scdn.co/image/ab6761610000e5eb214f3bc2bc97d9834125b273", 100000000, false, true, listOf("pop")),
        Artist("a2", "Ed Sheeran", "https://i.scdn.co/image/ab6761610000e5eb1ad50e05066a7b36029994cf", 80000000, false, true, listOf("pop"))
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
        // Implement real Spotify follow logic or just toggle local state
        BeatlyResult.Success(data = true)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed", e)
    }

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private val repositoryScope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.SupervisorJob() + kotlinx.coroutines.Dispatchers.Main)

    init {
        simulatePlayback()
    }

    private fun simulatePlayback() {
        repositoryScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                if (_playerState.value.isPlaying) {
                    _playerState.update { 
                        val nextPos = it.positionMs + 1000
                        if (nextPos >= it.durationMs && it.durationMs > 0) {
                            it.copy(isPlaying = false, positionMs = it.durationMs)
                        } else {
                            it.copy(positionMs = nextPos)
                        }
                    }
                }
            }
        }
    }

    // ── Library implementation ─────────────────────────────────────────────
    override fun getLibraryItems(): Flow<List<LibraryItem>> {
        // Implement using Room/Firestore
        return MutableStateFlow(emptyList())
    }

    override fun getLikedSongs(): Flow<List<UiSong>> {
        return songDao.getLikedSongs().map { entities ->
             entities.map { it.toUiModel() }
        }
    }

    override suspend fun toggleLikeSong(songId: String) {
        // Implement toggle logic
    }

    override suspend fun createLibraryPlaylist(name: String) {
        // Implement creation logic
    }

    // ── Player implementation ──────────────────────────────────────────────
    override suspend fun playSong(song: UiSong) {
        _playerState.update { it.copy(currentSong = song, isPlaying = true, positionMs = 0, durationMs = song.durationMs) }
    }

    override suspend fun togglePlayPause() {
        _playerState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    override suspend fun seekTo(positionMs: Long) {
        _playerState.update { it.copy(positionMs = positionMs) }
    }

    override suspend fun skipNext() {}
    override suspend fun skipPrevious() {}
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
