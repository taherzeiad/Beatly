package com.taher.beatly.domain.usecase

import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.SearchRepository
import com.taher.beatly.model.SearchFilter
import javax.inject.Inject

class SearchUseCase @Inject constructor(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(query: String, filter: SearchFilter): BeatlyResult<List<Any>> {
        if (query.isBlank()) return BeatlyResult.Success(emptyList())

        return when (filter) {
            SearchFilter.ARTISTS -> repository.searchArtists(query)
            SearchFilter.SONGS -> repository.searchSongs(query)
            SearchFilter.ALBUMS -> repository.searchAlbums(query)
            SearchFilter.PLAYLISTS -> repository.searchPlaylists(query)
            SearchFilter.TOP -> repository.searchArtists(query) // Default to artists for TOP for now
        }
    }
}
