package com.taher.beatly.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taher.beatly.model.Song
import com.taher.beatly.ui.components.BeatlyImage
import com.taher.beatly.ui.components.SectionHeader
import com.taher.beatly.ui.theme.SurfaceFill

@Composable
fun ExploreScreen(
    onSearchClick: () -> Unit,
    onGenreClick: (String) -> Unit,
    onSongClick: (Song) -> Unit,
    onSeeAllGenres: () -> Unit,
    trendingSongs: List<Song> = emptyList()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(Modifier.height(12.dp))
        
        // ── Search Field (ReadOnly-ish to trigger search screen) ───────────
        Box(modifier = Modifier.fillMaxWidth().clickable { onSearchClick() }) {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                placeholder = { Text("Search artists, songs...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        Spacer(Modifier.height(24.dp))
        
        // ── Genres ────────────────────────────────────────────────────────
        SectionHeader(title = "Genres", onSeeAllClick = onSeeAllGenres)
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            val genres = listOf("Dance", "Pop", "Jazz", "Classical", "Latin", "Electronic", "Rock", "Acoustic")
            items(genres) { genre ->
                FilterChip(
                    selected = false,
                    onClick = { onGenreClick(genre) },
                    label = { Text(genre) },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(containerColor = SurfaceFill)
                )
            }
        }

        Spacer(Modifier.height(24.dp))
        
        // ── Just For Your ─────────────────────────────────────────────────
        SectionHeader(title = "Just For Your", onSeeAllClick = {})
        Spacer(Modifier.height(12.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(trendingSongs) { song ->
                Column(modifier = Modifier.width(150.dp).clickable { onSongClick(song) }) {
                    BeatlyImage(url = song.imageUrl, modifier = Modifier.size(150.dp))
                    Spacer(Modifier.height(8.dp))
                    Text(song.title, style = MaterialTheme.typography.labelLarge, maxLines = 1)
                    Text(song.artistName, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        
        // ── Top Songs ─────────────────────────────────────────────────────
        SectionHeader(title = "Top Songs", onSeeAllClick = {})
        Spacer(Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            trendingSongs.forEachIndexed { index, song ->
                TopSongRow(index + 1, song, onSongClick)
            }
        }
        
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
fun TopSongRow(index: Int, song: Song, onClick: (Song) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(song) }
    ) {
        Text(
            text = "$index",
            modifier = Modifier.width(32.dp),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        BeatlyImage(
            url = song.imageUrl,
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, style = MaterialTheme.typography.labelLarge)
            Text(song.artistName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(onClick = {}) {
            Icon(Icons.Default.MoreHoriz, contentDescription = "More")
        }
    }
}
