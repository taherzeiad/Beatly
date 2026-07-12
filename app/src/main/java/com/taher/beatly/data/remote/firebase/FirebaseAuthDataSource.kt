package com.taher.beatly.data.remote.firebase

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    // ── Observe auth state ─────────────────────────────────────────────────
    @RequiresApi(Build.VERSION_CODES.O)
    val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val fbUser = firebaseAuth.currentUser
            if (fbUser == null) {
                trySend(null)
            } else {
                // fetch extra profile fields from Firestore
                firestore.collection("users").document(fbUser.uid).get()
                    .addOnSuccessListener { doc ->
                        trySend(
                            User(
                                id = fbUser.uid,
                                name = doc.getString("name") ?: fbUser.displayName ?: "",
                                email = fbUser.email ?: "",
                                username = doc.getString("username") ?: "",
                                avatarUrl = doc.getString("avatarUrl") ?: "",
                                isPremium = doc.getBoolean("isPremium") ?: false
                            )
                        )
                    }
                    .addOnFailureListener { trySend(null) }
            }
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    // ── Sign In ────────────────────────────────────────────────────────────
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun signIn(email: String, password: String): BeatlyResult<User> = try {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        val fbUser = result.user ?: return BeatlyResult.Error("User not found")
        val doc = firestore.collection("users").document(fbUser.uid).get().await()
        BeatlyResult.Success(
            User(
                id = fbUser.uid,
                name = doc.getString("name") ?: fbUser.displayName ?: "",
                email = fbUser.email ?: "",
                username = doc.getString("username") ?: "",
                avatarUrl = doc.getString("avatarUrl") ?: "",
                isPremium = doc.getBoolean("isPremium") ?: false
            )
        )
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Sign in failed", e)
    }

    // ── Sign Up ────────────────────────────────────────────────────────────
    suspend fun signUp(email: String, password: String, username: String): BeatlyResult<User> =
        try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val fbUser = result.user ?: return BeatlyResult.Error("User creation failed")

            // Save extra fields in Firestore
            val userDoc = mapOf(
                "name" to username,
                "username" to username,
                "email" to email,
                "avatarUrl" to "",
                "isPremium" to false,
                "createdAt" to com.google.firebase.Timestamp.now()
            )
            firestore.collection("users").document(fbUser.uid).set(userDoc).await()

            BeatlyResult.Success(
                User(
                    id = fbUser.uid,
                    name = username,
                    email = email,
                    username = username
                )
            )
        } catch (e: Exception) {
            BeatlyResult.Error(e.message ?: "Sign up failed", e)
        }

    // ── Sign Out ───────────────────────────────────────────────────────────
    suspend fun signOut(): BeatlyResult<Unit> = try {
        auth.signOut()
        BeatlyResult.Success(Unit)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Sign out failed", e)
    }

    // ── Password Reset ─────────────────────────────────────────────────────
    suspend fun sendPasswordReset(email: String): BeatlyResult<Unit> = try {
        auth.sendPasswordResetEmail(email).await()
        BeatlyResult.Success(Unit)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Reset failed", e)
    }

    // ── Delete Account ─────────────────────────────────────────────────────
    suspend fun deleteAccount(): BeatlyResult<Unit> = try {
        val uid = auth.currentUser?.uid ?: return BeatlyResult.Error("Not logged in")
        firestore.collection("users").document(uid).delete().await()
        auth.currentUser?.delete()?.await()
        BeatlyResult.Success(Unit)
    } catch (e: Exception) {
        BeatlyResult.Error(e.message ?: "Delete failed", e)
    }
}