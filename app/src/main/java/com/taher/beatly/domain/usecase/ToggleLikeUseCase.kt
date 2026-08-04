package com.taher.beatly.domain.usecase

import com.taher.beatly.domain.repository.LibraryRepository
import javax.inject.Inject

class ToggleLikeUseCase @Inject constructor(
    private val repository: LibraryRepository
) {
    suspend operator fun invoke(songId: String) {
        repository.toggleLikeSong(songId)
    }
}
