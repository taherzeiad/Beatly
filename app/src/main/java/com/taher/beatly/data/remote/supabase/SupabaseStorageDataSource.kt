package com.taher.beatly.data.remote.supabase

import android.util.Log
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.storage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SupabaseStorageDataSource @Inject constructor() {

    // ⚠️ Store in local.properties → BuildConfig
    private val supabaseUrl = "https://YOUR_PROJECT.supabase.co"
    private val supabaseKey = "YOUR_SUPABASE_ANON_KEY"

    private val client = createSupabaseClient(
        supabaseUrl = supabaseUrl,
        supabaseKey = supabaseKey
    ) { install(Storage) }

    // ── Upload avatar → returns public URL ─────────────────────────────────
    suspend fun uploadAvatar(userId: String, imageBytes: ByteArray): String? = try {
        val path = "avatars/$userId.jpg"
        val bucket = client.storage["beatly-images"]
        bucket.upload(path, imageBytes, upsert = true)
        bucket.publicUrl(path)
    } catch (e: Exception) {
        Log.e("Supabase", "Upload avatar failed", e)
        null
    }

    // ── Upload song cover → returns public URL ─────────────────────────────
    suspend fun uploadSongCover(songId: String, imageBytes: ByteArray): String? = try {
        val path = "songs/$songId.jpg"
        val bucket = client.storage["beatly-images"]
        bucket.upload(path, imageBytes, upsert = true)
        bucket.publicUrl(path)
    } catch (e: Exception) {
        Log.e("Supabase", "Upload cover failed", e)
        null
    }

    // ── Get public URL (no upload needed for Spotify images) ──────────────
    fun getPublicUrl(bucket: String, path: String): String =
        "$supabaseUrl/storage/v1/object/public/$bucket/$path"
}