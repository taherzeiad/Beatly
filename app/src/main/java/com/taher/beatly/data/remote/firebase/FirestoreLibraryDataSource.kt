package com.taher.beatly.data.remote.firebase


import com.taher.beatly.domain.model.*
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreLibraryDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    // ── Library (real-time) ────────────────────────────────────────────────
    fun getLibrary(userId: String): Flow<BeatlyResult<List<Playlist>>> = callbackFlow {
        val ref = firestore.collection("users").document(userId).collection("playlists")
        val listener = ref.addSnapshotListener { snap, err ->
            if (err != null) { trySend(BeatlyResult.Error(err.message ?: "Firestore error")); return@addSnapshotListener }
            val playlists = snap?.documents?.map { doc ->
                Playlist(
                    id          = doc.id,
                    name        = doc.getString("name")     ?: "",
                    imageUrl    = doc.getString("imageUrl") ?: "",
                    songCount   = (doc.getLong("songCount")  ?: 0L).toInt(),
                    artistCount = (doc.getLong("artistCount")?: 0L).toInt(),
                    ownerId     = userId
                )
            } ?: emptyList()
            trySend(BeatlyResult.Success(playlists))
        }
        awaitClose { listener.remove() }
    }

    // ── Create playlist ────────────────────────────────────────────────────
    suspend fun createPlaylist(userId: String, name: String): BeatlyResult<Playlist> = try {
        val ref  = firestore.collection("users").document(userId).collection("playlists")
        val data = mapOf("name" to name, "imageUrl" to "", "songCount" to 0, "artistCount" to 0, "createdAt" to Timestamp.now())
        val doc  = ref.add(data).await()
        BeatlyResult.Success(Playlist(id = doc.id, name = name, ownerId = userId))
    } catch (e: Exception) { BeatlyResult.Error(e.message ?: "Create failed", e) }

    // ── Delete playlist ────────────────────────────────────────────────────
    suspend fun deletePlaylist(playlistId: String, userId: String): BeatlyResult<Unit> = try {
        firestore.collection("users").document(userId).collection("playlists").document(playlistId).delete().await()
        BeatlyResult.Success(Unit)
    } catch (e: Exception) { BeatlyResult.Error(e.message ?: "Delete failed", e) }

    // ── Like / Unlike song ─────────────────────────────────────────────────
    suspend fun toggleLikeSong(userId: String, song: Song): BeatlyResult<Boolean> = try {
        val ref = firestore.collection("users").document(userId).collection("liked_songs").document(song.id)
        val doc = ref.get().await()
        if (doc.exists()) {
            ref.delete().await()
            BeatlyResult.Success(false)
        } else {
            ref.set(songToMap(song)).await()
            BeatlyResult.Success(true)
        }
    } catch (e: Exception) { BeatlyResult.Error(e.message ?: "Toggle like failed", e) }

    // ── Get liked songs (real-time) ─────────────────────────────────────────
    fun getLikedSongsFlow(userId: String): Flow<BeatlyResult<List<Song>>> = callbackFlow {
        val ref = firestore.collection("users").document(userId).collection("liked_songs")
        val listener = ref.addSnapshotListener { snap, err ->
            if (err != null) { trySend(BeatlyResult.Error(err.message ?: "Firestore error")); return@addSnapshotListener }
            val songs = snap?.documents?.map { doc -> mapToSong(doc.id, doc.data ?: emptyMap()) } ?: emptyList()
            trySend(BeatlyResult.Success(songs))
        }
        awaitClose { listener.remove() }
    }

    // ── Get followed artists (real-time) ────────────────────────────────────
    fun getFollowedArtistsFlow(userId: String): Flow<BeatlyResult<List<Artist>>> = callbackFlow {
        val ref = firestore.collection("users").document(userId).collection("followed_artists")
        val listener = ref.addSnapshotListener { snap, err ->
            if (err != null) { trySend(BeatlyResult.Error(err.message ?: "Firestore error")); return@addSnapshotListener }
            val artists = snap?.documents?.map { doc ->
                Artist(id = doc.id, name = doc.getString("name") ?: "", imageUrl = doc.getString("imageUrl") ?: "", isFollowing = true)
            } ?: emptyList()
            trySend(BeatlyResult.Success(artists))
        }
        awaitClose { listener.remove() }
    }

    // ── Get liked songs ────────────────────────────────────────────────────
    suspend fun getLikedSongs(userId: String): BeatlyResult<List<Song>> = try {
        val snap = firestore.collection("users").document(userId).collection("liked_songs").get().await()
        BeatlyResult.Success(snap.documents.map { mapToSong(it.id, it.data ?: emptyMap()) })
    } catch (e: Exception) { BeatlyResult.Error(e.message ?: "Fetch failed", e) }

    // ── Follow / Unfollow artist ───────────────────────────────────────────
    suspend fun toggleFollowArtist(userId: String, artist: Artist): BeatlyResult<Boolean> = try {
        val ref = firestore.collection("users").document(userId).collection("followed_artists").document(artist.id)
        val doc = ref.get().await()
        if (doc.exists()) { ref.delete().await(); BeatlyResult.Success(false) }
        else              { ref.set(mapOf("name" to artist.name, "imageUrl" to artist.imageUrl)).await(); BeatlyResult.Success(true) }
    } catch (e: Exception) { BeatlyResult.Error(e.message ?: "Toggle follow failed", e) }

    // ── Get followed artists ───────────────────────────────────────────────
    suspend fun getFollowedArtists(userId: String): BeatlyResult<List<Artist>> = try {
        val snap = firestore.collection("users").document(userId).collection("followed_artists").get().await()
        BeatlyResult.Success(snap.documents.map { doc ->
            Artist(id = doc.id, name = doc.getString("name") ?: "", imageUrl = doc.getString("imageUrl") ?: "", isFollowing = true)
        })
    } catch (e: Exception) { BeatlyResult.Error(e.message ?: "Fetch failed", e) }

    // ── Helpers ────────────────────────────────────────────────────────────
    private fun songToMap(song: Song) = mapOf(
        "title"      to song.title,
        "artistName" to song.artistName,
        "artistId"   to song.artistId,
        "imageUrl"   to song.imageUrl,
        "previewUrl" to song.previewUrl,
        "durationMs" to song.durationMs
    )

    private fun mapToSong(id: String, map: Map<String, Any?>) = Song(
        id         = id,
        title      = map["title"]      as? String ?: "",
        artistName = map["artistName"] as? String ?: "",
        artistId   = map["artistId"]   as? String ?: "",
        imageUrl   = map["imageUrl"]   as? String ?: "",
        previewUrl = map["previewUrl"] as? String ?: "",
        durationMs = map["durationMs"] as? Long   ?: 0L,
        isLiked    = true
    )
}