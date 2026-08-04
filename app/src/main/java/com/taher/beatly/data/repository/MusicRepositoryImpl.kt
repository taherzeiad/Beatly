package com.taher.beatly.data.repository

import com.taher.beatly.data.local.room.*
import com.taher.beatly.data.remote.spotify.*
import com.taher.beatly.data.mapper.*
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class MusicRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApiService,
    private val tokenManager: SpotifyTokenManager,
    private val songDao: SongDao,
    private val recentlyDao: RecentlyPlayedDao,
    private val artistDao: ArtistDao,
    private val artistPlayCountDao: ArtistPlayCountDao,
    private val libraryRepository: LibraryRepository,
    private val authRepository: AuthRepository,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : MusicRepository {

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

    override suspend fun getTrendingSongs(): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            if (token.isEmpty()) throw Exception("Invalid Spotify Token")

            val response = spotifyApi.search(query = "tag:new", type = "track", limit = 15, token = token)
            val trendingSongs = response.tracks?.items?.map { it.toDomain() } ?: emptyList()

            songDao.insertSongs(trendingSongs.map { it.toEntity() })
            BeatlyResult.Success(trendingSongs)
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Error getting trending songs", e)
            BeatlyResult.Success(getDummySongs().shuffled().take(10))
        }
    }

    override suspend fun getRecommendedSongs(): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            if (token.isEmpty()) throw Exception("Invalid Spotify Token")

            val recResponse = spotifyApi.getRecommendations(genres = "pop,dance,rock", limit = 15, token = token)
            val recommendedSongs = recResponse.tracks.map { it.toDomain() }

            songDao.insertSongs(recommendedSongs.map { it.toEntity() })
            BeatlyResult.Success(recommendedSongs)
        } catch (e: Exception) {
            android.util.Log.e("MusicRepository", "Error getting recommended songs", e)
            BeatlyResult.Success(getDummySongs().shuffled().take(8))
        }
    }

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
                    artistId = it.artistId,
                    imageUrl = it.imageUrl,
                    durationMs = it.durationMs,
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
                    artistId = it.artistId,
                    imageUrl = it.imageUrl,
                    durationMs = it.durationMs,
                )
            }
        }
    }

    override suspend fun getArtistDetail(artistId: String): BeatlyResult<Artist> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val artist = spotifyApi.getArtist(artistId, token)
            BeatlyResult.Success(artist.toDomain())
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

    override suspend fun getArtistTopTracks(artistId: String): BeatlyResult<List<Song>> = withContext(ioDispatcher) {
        try {
            val token = tokenManager.getValidToken()
            val response = spotifyApi.getArtistTopTracks(artistId, market = "US", token = token)
            BeatlyResult.Success(response.tracks.map { it.toDomain() })
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
            BeatlyResult.Success(response.items.map { it.toDomain() })
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }

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
        userId: String, song: Song
    ): BeatlyResult<Unit> = withContext(ioDispatcher) {
        try {
            recentlyDao.insert(
                RecentlyPlayedEntity(
                    id = song.id,
                    title = song.title,
                    artistName = song.artistName,
                    artistId = song.artistId,
                    imageUrl = song.imageUrl,
                    durationMs = song.durationMs,
                )
            )
            recentlyDao.trimOld()

            if (song.artistId.isNotEmpty()) {
                artistPlayCountDao.insert(
                    ArtistPlayCountEntity(
                        artistId = song.artistId,
                        name = song.artistName,
                        imageUrl = song.imageUrl
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
            val user = authRepository.currentUser.firstOrNull() ?: return@withContext BeatlyResult.Error("Not logged in")
            val token = tokenManager.getValidToken()
            val artist = spotifyApi.getArtist(artistId, token).toDomain()
            libraryRepository.toggleFollowArtist(user.id, artist)
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Failed", e)
        }
    }
}
