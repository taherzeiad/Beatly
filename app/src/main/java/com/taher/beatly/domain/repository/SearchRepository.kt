package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.*

interface SearchRepository {
    suspend fun searchArtists(query: String): BeatlyResult<List<Artist>>
    suspend fun searchSongs(query: String): BeatlyResult<List<Song>>
    suspend fun searchAlbums(query: String): BeatlyResult<List<Album>>
    suspend fun searchPlaylists(query: String): BeatlyResult<List<Playlist>>
}
