package com.taher.beatly.di


import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.media3.exoplayer.ExoPlayer
import com.taher.beatly.data.local.room.BeatlyDatabase
import com.taher.beatly.data.remote.spotify.SpotifyApiService
import com.taher.beatly.data.remote.spotify.SpotifyTokenService
import com.taher.beatly.data.repository.*
import com.taher.beatly.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // ── Dispatchers ──────────────────────────────────────────────────────────
    @Provides @Singleton @Named("IO")
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides @Singleton @Named("Main")
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    // ── Player ─────────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideExoPlayer(@ApplicationContext context: Context): ExoPlayer =
        ExoPlayer.Builder(context).build()

    // ── Firebase ───────────────────────────────────────────────────────────
    @Provides @Singleton fun provideFirebaseAuth()      = FirebaseAuth.getInstance()
    @Provides @Singleton fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    // ── OkHttp ────────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
            .build()

    // ── Spotify API Retrofit ───────────────────────────────────────────────
    @Provides @Singleton @Named("spotify")
    fun provideSpotifyRetrofit(okHttp: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.spotify.com/v1/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideSpotifyApiService(@Named("spotify") retrofit: Retrofit): SpotifyApiService =
        retrofit.create(SpotifyApiService::class.java)

    // ── Spotify Token Retrofit ─────────────────────────────────────────────
    @Provides @Singleton @Named("spotifyToken")
    fun provideSpotifyTokenRetrofit(okHttp: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://accounts.spotify.com/api/")
            .client(okHttp)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideSpotifyTokenService(@Named("spotifyToken") retrofit: Retrofit): SpotifyTokenService =
        retrofit.create(SpotifyTokenService::class.java)

    // ── Room ───────────────────────────────────────────────────────────────
    @Provides @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): BeatlyDatabase =
        Room.databaseBuilder(ctx, BeatlyDatabase::class.java, "beatly.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideSongDao(db: BeatlyDatabase)          = db.songDao()
    @Provides fun provideRecentlyPlayedDao(db: BeatlyDatabase) = db.recentlyPlayedDao()
    @Provides fun provideArtistDao(db: BeatlyDatabase)        = db.artistDao()
}

// ── Repository bindings ────────────────────────────────────────────────────

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds @Singleton
    abstract fun bindMusicRepository(impl: MusicRepositoryImpl): MusicRepository

    @Binds @Singleton
    abstract fun bindLibraryRepository(impl: LibraryRepositoryImpl): LibraryRepository

    @Binds @Singleton
    abstract fun bindSettingsRepository(impl: SettingsRepositoryImpl): SettingsRepository

    @Binds @Singleton
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
}