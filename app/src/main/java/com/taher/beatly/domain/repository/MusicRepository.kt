package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.*
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.PlayerState
import com.taher.beatly.model.Song as UiSong
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MusicRepository {
    fun getUserName(): Flow<String>
    suspend fun getTrendingSongs(): BeatlyResult<List<Song>>
    suspend fun getRecommendedSongs(): BeatlyResult<List<Song>>
    suspend fun getTopArtists(): BeatlyResult<List<Artist>>
    fun getUserTopArtists(): Flow<List<Artist>>
    suspend fun getRecentlyPlayed(userId: String): BeatlyResult<List<Song>>
    fun getRecentlyPlayedFlow(userId: String): Flow<List<Song>>
    suspend fun getArtistDetail(artistId: String): BeatlyResult<Artist>
    suspend fun getArtistTopTracks(artistId: String): BeatlyResult<List<Song>>
    suspend fun getGenreTracks(genreName: String): BeatlyResult<List<Song>>
    suspend fun getPlaylistTracks(playlistId: String): BeatlyResult<List<Song>>
    suspend fun getAlbumTracks(albumId: String): BeatlyResult<List<Song>>
    suspend fun getGenres(): BeatlyResult<List<Genre>>
    suspend fun addToRecentlyPlayed(userId: String, song: Song): BeatlyResult<Unit>
    suspend fun toggleFollowArtist(artistId: String): BeatlyResult<Boolean>
}
