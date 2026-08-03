package com.taher.beatly.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayMusicScreen(
    onBackClick   : () -> Unit,
    viewModel     : PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val song = uiState.song
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.onErrorShown()
        }
    }

    var showLyrics by remember { mutableStateOf(false) }

    if (showLyrics) {
        ModalBottomSheet(onDismissRequest = { showLyrics = false }) {
            Column(
                Modifier.fillMaxWidth().padding(24.dp).padding(bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Lyrics", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                Text(
                    "Lyrics are currently unavailable for this track.\nCheck back later!",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier           = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment  = Alignment.CenterVertically
            ) {
                RoundIconButton(
                    icon               = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick            = onBackClick,
                    contentDescription = "Back"
                )
                Text("Music", style = MaterialTheme.typography.titleMedium)
                RoundIconButton(icon = Icons.Filled.MoreVert, onClick = { }, contentDescription = "More", hasBorder = true)
            }

            if (song == null) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No song selected", style = MaterialTheme.typography.titleLarge)
                }
            } else {
                Spacer(Modifier.height(20.dp))
                com.taher.beatly.ui.components.BeatlyImage(
                    url = song.imageUrl,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(Modifier.height(24.dp))
                Text(song.title, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.align(Alignment.CenterHorizontally))
                Text("Ft. ${song.artistName}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.align(Alignment.CenterHorizontally))

                Spacer(Modifier.height(16.dp))
                Slider(
                    value = uiState.positionMs.toFloat(),
                    onValueChange = { viewModel.onSeek(it.toLong()) },
                    valueRange = 0f..uiState.durationMs.toFloat(),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Gray200
                    )
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatDuration(uiState.positionMs), style = MaterialTheme.typography.bodySmall)
                    Text(formatDuration(uiState.durationMs), style = MaterialTheme.typography.bodySmall)
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = viewModel::onSkipPrevious) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = viewModel::onSeekBackward) {
                        Icon(Icons.Filled.Replay10, contentDescription = "Replay 10s", modifier = Modifier.size(28.dp))
                    }
                    PlayPauseButton(isPlaying = uiState.isPlaying, onClick = viewModel::onPlayPauseClicked)
                    IconButton(onClick = viewModel::onSeekForward) {
                        Icon(Icons.Filled.Forward10, contentDescription = "Forward 10s", modifier = Modifier.size(28.dp))
                    }
                    IconButton(onClick = viewModel::onSkipNext) {
                        Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier.size(32.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Speed, contentDescription = "Playback speed") }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Timer, contentDescription = "Sleep timer") }
                    IconButton(onClick = {}) { Icon(Icons.Filled.Cast, contentDescription = "Cast") }
                    IconButton(onClick = {}) { Icon(Icons.Filled.MoreVert, contentDescription = "More options") }
                }

                Spacer(Modifier.height(20.dp))
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showLyrics = true }
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                    Text("Lyrics", style = MaterialTheme.typography.labelLarge)
                }
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}

@Composable
private fun PlayPauseButton(isPlaying: Boolean, onClick: () -> Unit) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(50)),
        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Icon(
            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
            contentDescription = if (isPlaying) "Pause" else "Play",
            tint = MaterialTheme.colorScheme.background,
            modifier = Modifier.size(28.dp)
        )
    }
}

private fun formatDuration(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return "%02d:%02d".format(minutes, seconds)
}
