package com.taher.beatly.data.local.room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// ── Entities ───────────────────────────────────────────────────────────────

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id          : String,
    val title       : String,
    val artistName  : String,
    val artistId    : String = "",
    val albumName   : String = "",
    val imageUrl    : String = "",
    val previewUrl  : String = "",
    val durationMs  : Long   = 0L,
    val isLiked     : Boolean = false,
    val isSaved     : Boolean = false,
)

@Entity(tableName = "recently_played")
data class RecentlyPlayedEntity(
    @PrimaryKey val id : String,
    val title : String,
    val artistName : String,
    val artistId : String = "",
    val imageUrl : String = "",
    val previewUrl : String = "",
    val durationMs : Long = 0L,
    val playedAt : Long = System.currentTimeMillis()
)

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey val id              : String,
    val name             : String,
    val imageUrl         : String = "",
    val monthlyListeners : Long   = 0L,
    val isFollowing      : Boolean = false
)

@Entity(tableName = "artist_play_counts")
data class ArtistPlayCountEntity(
    @PrimaryKey val artistId: String,
    val name: String,
    val imageUrl: String,
    val playCount: Int = 0
)

// ── DAOs ───────────────────────────────────────────────────────────────────

@Dao
interface SongDao {
    @Query("SELECT * FROM songs WHERE isLiked = 1")
    fun getLikedSongs(): Flow<List<SongEntity>>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: String): SongEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("UPDATE songs SET isLiked = :liked WHERE id = :id")
    suspend fun setLiked(id: String, liked: Boolean)

    @Query("DELETE FROM songs")
    suspend fun clearAll()
}

@Dao
interface RecentlyPlayedDao {
    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 20")
    fun getRecentlyPlayedFlow(): Flow<List<RecentlyPlayedEntity>>

    @Query("SELECT * FROM recently_played ORDER BY playedAt DESC LIMIT 20")
    suspend fun getRecentlyPlayed(): List<RecentlyPlayedEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(song: RecentlyPlayedEntity)

    @Query("DELETE FROM recently_played WHERE id NOT IN (SELECT id FROM recently_played ORDER BY playedAt DESC LIMIT 20)")
    suspend fun trimOld()
}

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artists WHERE isFollowing = 1")
    fun getFollowedArtists(): Flow<List<ArtistEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtists(artists: List<ArtistEntity>)

    @Query("UPDATE artists SET isFollowing = :following WHERE id = :id")
    suspend fun setFollowing(id: String, following: Boolean)
}

@Dao
interface ArtistPlayCountDao {
    @Query("SELECT * FROM artist_play_counts ORDER BY playCount DESC LIMIT 20")
    fun getTopArtistsFlow(): Flow<List<ArtistPlayCountEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: ArtistPlayCountEntity)

    @Query("UPDATE artist_play_counts SET playCount = playCount + 1 WHERE artistId = :artistId")
    suspend fun incrementPlayCount(artistId: String)
}

// ── Database ───────────────────────────────────────────────────────────────

@Database(
    entities = [SongEntity::class, RecentlyPlayedEntity::class, ArtistEntity::class, ArtistPlayCountEntity::class],
    version  = 3,
    exportSchema = false
)
abstract class BeatlyDatabase : RoomDatabase() {
    abstract fun songDao()          : SongDao
    abstract fun recentlyPlayedDao(): RecentlyPlayedDao
    abstract fun artistDao()        : ArtistDao
    abstract fun artistPlayCountDao(): ArtistPlayCountDao
}
