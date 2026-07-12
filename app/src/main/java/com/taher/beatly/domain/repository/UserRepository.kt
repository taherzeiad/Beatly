package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.User

interface UserRepository {
    suspend fun getProfile(userId: String): BeatlyResult<User>
    suspend fun updateProfile(user: User): BeatlyResult<Unit>
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): BeatlyResult<String>
}
