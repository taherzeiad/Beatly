package com.taher.beatly.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.model.Song
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton

/**
 * Stateful entry point wired to Hilt + the real ViewModel.
 * This is what the NavGraph calls.
 */
@Composable
fun LikedSongsScreen(
    onBackClick : () -> Unit,
    viewModel   : LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.likedSongsState.collectAsStateWithLifecycle()

    LikedSongsContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onLikeClick = viewModel::onLikeToggled,
        onSongClick = viewModel::onPlaySong
    )
}

/**
 * Stateless UI layer – takes plain data and lambdas only, no ViewModel/Hilt.
 * Because it has no dependency on hiltViewModel(), it can be rendered directly
 * inside @Preview (Android Studio's "Split"/"Design" preview pane) with fake
 * data, so you can tweak spacing/colors/layout and see results instantly
 * without running the app on a device or emulator.
 */
@Composable
fun LikedSongsContent(
    uiState: LikedSongsUiState,
    onBackClick: () -> Unit = {},
    onLikeClick: (String) -> Unit = {},
    onSongClick: (Song) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .background(MaterialTheme.colorScheme.outline)
        ) {
            PlaceholderImage(modifier = Modifier.fillMaxSize(), shape = RoundedCornerShape(0.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 12.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoundIconButton(
                    icon               = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick            = onBackClick,
                    contentDescription = "Back"
                )
                RoundIconButton(icon = Icons.Filled.MoreHoriz, onClick = { }, contentDescription = "More")
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(16.dp))
            LikedSongsHeader(songCount = uiState.songs.size)

            Spacer(Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(uiState.songs, key = { it.id }) { song ->
                    LikedSongRow(
                        song        = song,
                        onLikeClick = { onLikeClick(song.id) },
                        onClick     = { onSongClick(song) }
                    )
                }
            }
        }
    }
}

@Composable
private fun LikedSongsHeader(songCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("Liked songs", style = MaterialTheme.typography.titleLarge)
            Text("$songCount songs", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.PlayArrow, contentDescription = "Play all", tint = MaterialTheme.colorScheme.background)
        }
    }
}

@Composable
private fun LikedSongRow(song: Song, onLikeClick: () -> Unit, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaceholderImage(modifier = Modifier.size(56.dp), shape = RoundedCornerShape(12.dp), showLabel = false)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, style = MaterialTheme.typography.labelLarge)
            Text(song.artistName, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        }
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (song.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = "Like",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// =========================================================================
// Previews – open this file in Android Studio and use the "Split" or
// "Design" tab (or the small "Preview" gutter icon next to each fun) to see
// the screen render instantly. Edit any composable above and hit the
// refresh icon on the preview pane (or Build > Rebuild if it's stale) to
// see your change without installing the app on a device/emulator.
// =========================================================================

private val previewLikedSongs = listOf(
    Song(id = "s1", title = "Sharks", artistName = "Imagine Dragons", artistId = "a9", isLiked = true),
    Song(id = "s2", title = "God Is a Woman", artistName = "Ariana Grande", artistId = "a10", isLiked = true),
    Song(id = "s4", title = "Ghost", artistName = "Justin Bieber", artistId = "a1", isLiked = true)
)

private val previewLikedSongsUiState = LikedSongsUiState(
    songs = previewLikedSongs,
    currentlyPlayingSongId = "s4"
)

@Preview(showBackground = true, name = "Liked Songs – Light")
@Composable
private fun LikedSongsScreenPreview() {
    MaterialTheme {
        LikedSongsContent(uiState = previewLikedSongsUiState)
    }
}

@Preview(
    showBackground = true,
    name = "Liked Songs – Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LikedSongsScreenPreviewDark() {
    MaterialTheme {
        LikedSongsContent(uiState = previewLikedSongsUiState)
    }
}

@Preview(showBackground = true, name = "Liked Songs – Empty state")
@Composable
private fun LikedSongsScreenEmptyPreview() {
    MaterialTheme {
        LikedSongsContent(uiState = LikedSongsUiState(songs = emptyList()))
    }
}