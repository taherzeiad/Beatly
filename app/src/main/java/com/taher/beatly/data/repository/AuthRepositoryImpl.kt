package com.taher.beatly.data.repository

import android.annotation.SuppressLint
import com.taher.beatly.data.local.datastore.SettingsDataStore
import com.taher.beatly.data.remote.firebase.FirebaseAuthDataSource
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.User
import com.taher.beatly.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: FirebaseAuthDataSource,
    private val dataStore     : SettingsDataStore
) : AuthRepository {

    @SuppressLint("NewApi")
    override val currentUser: Flow<User?> = authDataSource.currentUser

    @SuppressLint("NewApi")
    override suspend fun signIn(email: String, password: String): BeatlyResult<User> {
        val result = authDataSource.signIn(email, password)
        if (result is BeatlyResult.Success) dataStore.setUserId(result.data.id)
        return result
    }

    override suspend fun signUp(email: String, password: String, username: String): BeatlyResult<User> {
        val result = authDataSource.signUp(email, password, username)
        if (result is BeatlyResult.Success) dataStore.setUserId(result.data.id)
        return result
    }

    override suspend fun signOut(): BeatlyResult<Unit> {
        val result = authDataSource.signOut()
        if (result is BeatlyResult.Success) dataStore.clearAll()
        return result
    }

    override suspend fun sendPasswordReset(email: String) = authDataSource.sendPasswordReset(email)
    override suspend fun deleteAccount()                  = authDataSource.deleteAccount()
}