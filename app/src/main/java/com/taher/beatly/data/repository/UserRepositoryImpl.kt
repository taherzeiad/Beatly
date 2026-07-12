package com.taher.beatly.data.repository

import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.User
import com.taher.beatly.domain.repository.UserRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun getProfile(userId: String): BeatlyResult<User> = try {
        val doc = firestore.collection("users").document(userId).get().await()
        if (doc.exists()) {
            BeatlyResult.Success(User(
                id = doc.id,
                name = doc.getString("name") ?: "",
                email = doc.getString("email") ?: "",
                username = doc.getString("username") ?: "",
                avatarUrl = doc.getString("avatarUrl") ?: "",
                isPremium = doc.getBoolean("isPremium") ?: false
            ))
        } else {
            BeatlyResult.Error("User not found")
        }
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Failed to fetch profile", e)
    }

    override suspend fun updateProfile(user: User): BeatlyResult<Unit> = try {
        val data = mapOf(
            "name" to user.name,
            "username" to user.username,
            "avatarUrl" to user.avatarUrl
        )
        firestore.collection("users").document(user.id).update(data).await()
        BeatlyResult.Success(Unit)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Update failed", e)
    }

    override suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): BeatlyResult<String> {
        // Implement Supabase or Firebase Storage upload
        return BeatlyResult.Error("Not implemented")
    }
}
