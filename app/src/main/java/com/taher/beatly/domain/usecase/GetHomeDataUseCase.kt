package com.taher.beatly.domain.usecase

import com.taher.beatly.domain.model.*
import com.taher.beatly.domain.repository.MusicRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

data class HomeData(
    val trendingSongs: List<Song>,
    val topArtists: List<Artist>
)

class GetHomeDataUseCase @Inject constructor(
    private val repository: MusicRepository
) {
    suspend operator fun invoke(): BeatlyResult<HomeData> = coroutineScope {
        val trendingDeferred = async { repository.getTrendingSongs() }
        val artistsDeferred = async { repository.getTopArtists() }

        val trendingResult = trendingDeferred.await()
        val artistsResult = artistsDeferred.await()

        if (trendingResult is BeatlyResult.Success && artistsResult is BeatlyResult.Success) {
            BeatlyResult.Success(
                HomeData(
                    trendingSongs = trendingResult.data,
                    topArtists = artistsResult.data
                )
            )
        } else {
            val errorMsg = (trendingResult as? BeatlyResult.Error)?.message 
                ?: (artistsResult as? BeatlyResult.Error)?.message 
                ?: "Failed to load home data"
            BeatlyResult.Error(errorMsg)
        }
    }
}
