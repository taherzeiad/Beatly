package com.taher.beatly.ui.player

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton
import java.util.concurrent.TimeUnit

@Composable
fun PlayMusicScreen(
    onBackClick   : () -> Unit,
    onLyricsClick : () -> Unit = {},
    viewModel     : PlayerViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val song = uiState.song ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
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
            RoundIconButton(icon = Icons.Filled.MoreVert, onClick = { }, contentDescription = "More")
        }

        Spacer(Modifier.height(20.dp))
        com.taher.beatly.ui.components.BeatlyImage(
            url = song.imageUrl,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(24.dp)
        )

        Spacer(Modifier.height(24.dp))
        Text(song.title, style = MaterialTheme.typography.titleLarge)
        Text("Ft. ${song.artistName}", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)

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
            Text(formatDuration(uiState.positionMs))
            Text(formatDuration(uiState.durationMs))
        }

        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier
                .size(32.dp)
                .clickableNoRipple(viewModel::onSkipPrevious))
            Icon(Icons.Filled.Replay10, contentDescription = "Replay 10s", modifier = Modifier.size(28.dp))
            PlayPauseButton(isPlaying = uiState.isPlaying, onClick = viewModel::onPlayPauseClicked)
            Icon(Icons.Filled.Forward10, contentDescription = "Forward 10s", modifier = Modifier.size(28.dp))
            Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier
                .size(32.dp)
                .clickableNoRipple(viewModel::onSkipNext))
        }

        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(Icons.Outlined.Speed, contentDescription = "Playback speed")
            Icon(Icons.Outlined.Timer, contentDescription = "Sleep timer")
            Icon(Icons.Filled.Cast, contentDescription = "Cast")
            Icon(Icons.Filled.MoreVert, contentDescription = "More options")
        }

        Spacer(Modifier.height(12.dp))
        TextButton(
            onClick = onLyricsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                Text("Lyrics")
            }
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

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
)

private fun formatDuration(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return "%02d:%02d".format(minutes, seconds)
}
