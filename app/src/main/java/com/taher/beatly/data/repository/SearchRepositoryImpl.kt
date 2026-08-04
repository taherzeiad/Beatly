package com.taher.beatly.data.repository

import com.taher.beatly.data.remote.spotify.SpotifyApiService
import com.taher.beatly.data.remote.spotify.SpotifyTokenManager
import com.taher.beatly.data.mapper.toDomain
import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.SearchRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SearchRepositoryImpl @Inject constructor(
    private val spotifyApi: SpotifyApiService,
    private val tokenManager: SpotifyTokenManager,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : SearchRepository {

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
}
