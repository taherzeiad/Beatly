package com.taher.beatly.domain.repository

import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signIn(email: String, password: String): BeatlyResult<User>
    suspend fun signUp(email: String, password: String, username: String): BeatlyResult<User>
    suspend fun signOut(): BeatlyResult<Unit>
    suspend fun sendPasswordReset(email: String): BeatlyResult<Unit>
    suspend fun deleteAccount(): BeatlyResult<Unit>
}
