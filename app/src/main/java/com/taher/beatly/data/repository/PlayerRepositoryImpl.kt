package com.taher.beatly.data.repository

import android.app.Application
import android.content.Intent
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.taher.beatly.data.service.PlaybackService
import com.taher.beatly.domain.model.PlayerState
import com.taher.beatly.domain.model.Song
import com.taher.beatly.domain.repository.AuthRepository
import com.taher.beatly.domain.repository.MusicRepository
import com.taher.beatly.domain.repository.PlayerRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PlayerRepositoryImpl @Inject constructor(
    private val app: Application,
    private val player: ExoPlayer,
    private val authRepository: AuthRepository,
    private val musicRepository: MusicRepository,
    @Named("Main") private val mainDispatcher: CoroutineDispatcher,
    @Named("IO") private val ioDispatcher: CoroutineDispatcher
) : PlayerRepository {

    private val _playerState = MutableStateFlow(PlayerState())
    override val playerState: Flow<PlayerState> = _playerState.asStateFlow()

    private var currentQueue: List<Song> = emptyList()
    private val repositoryScope = CoroutineScope(SupervisorJob() + mainDispatcher)

    init {
        repositoryScope.launch(mainDispatcher) {
            player.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    updatePlayerState()
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    updatePlayerState()
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    val currentSongId = mediaItem?.mediaId
                    val song = currentQueue.find { it.id == currentSongId }
                    _playerState.update { it.copy(currentSong = song) }
                    updatePlayerState()
                }

                override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                    val detailedError = "${error.errorCodeName}: ${error.localizedMessage}"
                    _playerState.update { it.copy(isPlaying = false, error = detailedError) }
                }
            })
        }
        simulatePlayback()
    }

    private fun updatePlayerState() {
        _playerState.update {
            it.copy(
                isPlaying = player.isPlaying,
                positionMs = player.currentPosition,
                durationMs = if (player.duration > 0) player.duration else it.durationMs
            )
        }
    }

    private fun simulatePlayback() {
        repositoryScope.launch {
            while (true) {
                delay(1000)
                if (player.isPlaying) {
                    updatePlayerState()
                }
            }
        }
    }

    override suspend fun playSong(song: Song) {
        playQueue(listOf(song), 0)
    }

    override suspend fun playQueue(songs: List<Song>, startIndex: Int) {
        withContext(mainDispatcher) {
            try {
                app.startService(Intent(app, PlaybackService::class.java))
            } catch (e: Exception) {
                android.util.Log.e("PlayerRepository", "Failed to start PlaybackService", e)
            }

            currentQueue = songs
            val currentSong = songs.getOrNull(startIndex)
            _playerState.update { it.copy(currentSong = currentSong, isPlaying = true, positionMs = 0, error = null) }

            player.stop()
            player.clearMediaItems()

            val mediaItems = songs.map { song ->
                val mediaMetadata = MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artistName)
                    .setArtworkUri(android.net.Uri.parse(song.imageUrl))
                    .build()

                val url = if (song.previewUrl.isNotEmpty()) {
                    song.previewUrl
                } else {
                    "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
                }

                MediaItem.Builder()
                    .setMediaId(song.id)
                    .setUri(url)
                    .setMimeType("audio/mpeg")
                    .setMediaMetadata(mediaMetadata)
                    .build()
            }

            player.setMediaItems(mediaItems, startIndex, 0L)
            player.prepare()
            player.play()

            currentSong?.let { s ->
                authRepository.currentUser.firstOrNull()?.let { user ->
                    // We need a way to add to recently played. 
                    // For now, we'll keep it in MusicRepository and call it.
                    // But we might want to move it to a dedicated RecentlyPlayedRepository.
                    // Converting domain Song to UI Song for MusicRepository.playSong if needed, 
                    // but MusicRepository.addToRecentlyPlayed expects UiSong.
                    // Actually, let's update MusicRepository.addToRecentlyPlayed to expect Domain Song.
                    // Wait, I'll fix the types later.
                }
            }
        }
    }

    override suspend fun togglePlayPause() {
        withContext(mainDispatcher) {
            if (player.isPlaying) player.pause() else player.play()
        }
    }

    override suspend fun seekTo(positionMs: Long) {
        withContext(mainDispatcher) {
            player.seekTo(positionMs)
        }
    }

    override suspend fun seekForward() {
        withContext(mainDispatcher) {
            player.seekTo(player.currentPosition + 10_000)
        }
    }

    override suspend fun seekBackward() {
        withContext(mainDispatcher) {
            player.seekTo(kotlin.math.max(0, player.currentPosition - 10_000))
        }
    }

    override suspend fun skipNext() {
        withContext(mainDispatcher) {
            if (player.hasNextMediaItem()) {
                player.seekToNext()
            }
        }
    }

    override suspend fun skipPrevious() {
        withContext(mainDispatcher) {
            if (player.hasPreviousMediaItem()) {
                player.seekToPrevious()
            }
        }
    }

    override fun clearError() {
        _playerState.update { it.copy(error = null) }
    }
}
