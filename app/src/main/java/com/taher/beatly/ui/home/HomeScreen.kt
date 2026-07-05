package com.taher.beatly.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Song
import com.taher.beatly.ui.components.*

@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSeeAllTrendingClick: () -> Unit,
    onSeeAllArtistsClick: () -> Unit,
    onSeeAllRecentClick: () -> Unit,
    onArtistClick: (String) -> Unit,
    onNavigateTab: (BeatlyTab) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BeatlyBottomBar(selectedTab = BeatlyTab.HOME, onTabSelected = onNavigateTab) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            HomeHeader(userName = uiState.userName, onSearchClick = onSearchClick)

            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Trending Now", onSeeAllClick = onSeeAllTrendingClick)
            Spacer(Modifier.height(12.dp))
            TrendingRow(songs = uiState.trendingSongs)

            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Top Artist", onSeeAllClick = onSeeAllArtistsClick)
            Spacer(Modifier.height(12.dp))
            TopArtistRow(artists = uiState.topArtists, onArtistClick = onArtistClick)

            Spacer(Modifier.height(24.dp))
            SectionHeader(title = "Recently Played", onSeeAllClick = onSeeAllRecentClick)
            Spacer(Modifier.height(12.dp))
            uiState.recentlyPlayed.firstOrNull()?.let { song ->
                SongRow(
                    song = song,
                    onLikeClick = { viewModel.onLikeToggled(song.id) },
                    onPlayClick = { viewModel.onPlayPauseToggled(song) },
                    onPauseClick = { viewModel.onPlayPauseToggled(song) },
                    isCurrentlyPlaying = uiState.currentlyPlayingSongId == song.id
                )
            }
            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HomeHeader(userName: String, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlaceholderImage(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                showLabel = false
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Good Morning!",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(userName, style = MaterialTheme.typography.titleMedium)
            }
        }
        Row {
            RoundIconButton(
                icon = Icons.Filled.Search,
                onClick = onSearchClick,
                contentDescription = "Search"
            )
            Spacer(Modifier.width(8.dp))
            RoundIconButton(
                icon = Icons.Filled.Notifications,
                onClick = {},
                contentDescription = "Notifications"
            )
        }
    }
}

@Composable
private fun TrendingRow(songs: List<Song>) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(songs, key = { it.id }) { song ->
            Column(modifier = Modifier.width(150.dp)) {
                PlaceholderImage(modifier = Modifier.size(150.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    song.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.artistName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun TopArtistRow(artists: List<Artist>, onArtistClick: (String) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        items(artists, key = { it.id }) { artist ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(90.dp)
            ) {
                PlaceholderImage(
                    modifier = Modifier
                        .size(90.dp),
                    shape = CircleShape,
                    showLabel = false
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    artist.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onArtistClick(artist.id) }
                )
            }
        }
    }
}


