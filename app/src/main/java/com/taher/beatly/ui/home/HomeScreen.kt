package com.taher.beatly.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.model.Artist
import com.taher.beatly.model.Song
import com.taher.beatly.ui.components.*

/**
 * Stateful entry point wired to Hilt + the real ViewModel.
 * This is what the NavGraph calls.
 */
@Composable
fun HomeScreen(
    onSearchClick: () -> Unit,
    onSeeAllTrendingClick: () -> Unit,
    onSeeAllArtistsClick: () -> Unit,
    onSeeAllRecentClick: () -> Unit,
    onArtistClick         : (String) -> Unit,
    onSongClick           : (Song) -> Unit,
    onNavigateTab         : (BeatlyTab) -> Unit,
    viewModel             : HomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreenContent(
        uiState = uiState,
        onSearchClick = onSearchClick,
        onSeeAllTrendingClick = onSeeAllTrendingClick,
        onSeeAllArtistsClick = onSeeAllArtistsClick,
        onSeeAllRecentClick = onSeeAllRecentClick,
        onArtistClick = onArtistClick,
        onSongClick = { song ->
            viewModel.onPlayPauseToggled(song)
            onSongClick(song)
        },
        onNavigateTab = onNavigateTab,
        onLikeClick = viewModel::onLikeToggled,
        onPlayPauseClick = viewModel::onPlayPauseToggled
    )
}

/**
 * Stateless UI layer — takes plain data and lambdas only, no ViewModel/Hilt.
 * Because it has no dependency on hiltViewModel(), it can be rendered directly
 * inside @Preview (Android Studio's "Split"/"Design" preview pane) with fake
 * data, so you can tweak spacing/colors/layout and see results instantly
 * without running the app on a device or emulator.
 */
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onSearchClick: () -> Unit = {},
    onSeeAllTrendingClick: () -> Unit = {},
    onSeeAllArtistsClick: () -> Unit = {},
    onSeeAllRecentClick : () -> Unit = {},
    onArtistClick       : (String) -> Unit = {},
    onSongClick         : (Song) -> Unit = {},
    onNavigateTab       : (BeatlyTab) -> Unit = {},
    onLikeClick         : (String) -> Unit = {},
    onPlayPauseClick    : (Song) -> Unit = {},
) {
    Scaffold(
        bottomBar = {
            BeatlyBottomBar(
                selectedTab   = BeatlyTab.HOME,
                onTabSelected = onNavigateTab
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            HomeHeader(uiState = uiState, onSearchClick = onSearchClick)

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                val scrollState = androidx.compose.foundation.rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scrollState)) {
                    Spacer(Modifier.height(24.dp))
                    HomePromotedCard()

                    Spacer(Modifier.height(24.dp))
                    SectionHeader(title = "Trending Now", onSeeAllClick = onSeeAllTrendingClick)
                    Spacer(Modifier.height(12.dp))
                    if (uiState.trendingSongs.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        TrendingRow(songs = uiState.trendingSongs, onSongClick = onSongClick)
                    }

                    Spacer(Modifier.height(24.dp))
                    SectionHeader(title = "Top Artist", onSeeAllClick = onSeeAllArtistsClick)
                    Spacer(Modifier.height(12.dp))
                    if (uiState.topArtists.isEmpty()) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else {
                        TopArtistRow(artists = uiState.topArtists, onArtistClick = onArtistClick)
                    }

                    Spacer(Modifier.height(24.dp))
                    SectionHeader(title = "Recently Played", onSeeAllClick = onSeeAllRecentClick)
                    Spacer(Modifier.height(12.dp))
                    if (uiState.recentlyPlayed.isEmpty()) {
                        Text(
                            "No recently played songs",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            uiState.recentlyPlayed.take(3).forEach { song ->
                                SongRow(
                                    song = song,
                                    onLikeClick = { onLikeClick(song.id) },
                                    onPlayClick = { onSongClick(song) },
                                    onPauseClick = { onPlayPauseClick(song) },
                                    isCurrentlyPlaying = uiState.currentlyPlayingSongId == song.id
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun HomePromotedCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "New Album",
                    color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.labelMedium
                )
                Text(
                    "Happier Than Ever",
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    "Billie Eilish",
                    color = MaterialTheme.colorScheme.background,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            PlaceholderImage(modifier = Modifier.size(100.dp), shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp), showLabel = false)
        }
    }
}

@Composable
private fun HomeHeader(uiState: HomeUiState, onSearchClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            PlaceholderImage(
                modifier = Modifier.size(56.dp), shape = CircleShape, showLabel = false
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    uiState.greeting,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(uiState.userName, fontSize = 17.sp, style = MaterialTheme.typography.titleMedium)
            }
        }
        Row {
            RoundIconButton(
                icon = Icons.Filled.Search, onClick = onSearchClick, contentDescription = "Search"
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
private fun TrendingRow(songs: List<Song>, onSongClick: (Song) -> Unit) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(songs, key = { it.id }) { song ->
            Column(modifier = Modifier.width(150.dp).clickable { onSongClick(song) }) {
                BeatlyImage(url = song.imageUrl, modifier = Modifier.size(150.dp))
                Spacer(Modifier.height(8.dp))
                Text(
                    song.title,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    song.artistName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    fontSize = 12.sp,
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
                modifier = Modifier.width(90.dp).clickable { onArtistClick(artist.id) }
            ) {
                BeatlyImage(
                    url = artist.imageUrl,
                    modifier = Modifier.size(90.dp),
                    shape = CircleShape
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    artist.name,
                    maxLines = 1,
                    fontSize = 13.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────
// Previews — open this file in Android Studio and use the "Split" or
// "Design" tab (or the small "Preview" gutter icon next to each fun) to see
// the screen render instantly. Edit any composable above and hit the
// refresh icon on the preview pane (or Build > Rebuild if it's stale) to
// see your change without installing the app on a device/emulator.
// ─────────────────────────────────────────────────────────────────────────

private val previewSongs = listOf(
    Song(
        id = "s1", title = "Sharks", artistName = "Imagine Dragons", artistId = "a9", isLiked = true
    ),
    Song(
        id = "s2",
        title = "God Is a Woman",
        artistName = "Ariana Grande",
        artistId = "a10",
        isLiked = true
    ),
    Song(id = "s3", title = "Handsome", artistName = "Warren Hue", artistId = "a11", isLiked = true)
)

private val previewArtists = listOf(
    Artist(id = "a5", name = "Khalid", isVerified = true),
    Artist(id = "a3", name = "Taylor Swift", isVerified = true),
    Artist(id = "a1", name = "Justin Bieber", isVerified = true),
    Artist(id = "a8", name = "Paramore")
)

private val previewUiState = HomeUiState(
    userName = "Mr. Aiden Smith",
    trendingSongs = previewSongs,
    topArtists = previewArtists,
    recentlyPlayed = listOf(
        Song(
            id = "s4",
            title = "Ghost",
            artistName = "Justin Bieber",
            artistId = "a1",
            isLiked = true
        )

    ),
    currentlyPlayingSongId = "s4",
    isLoading = false
)

@Preview(showBackground = true, name = "Home – Light")
@Composable
private fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreenContent(uiState = previewUiState)
    }
}

@Preview(
    showBackground = true,
    name = "Home – Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HomeScreenPreviewDark() {
    MaterialTheme {
        HomeScreenContent(uiState = previewUiState)
    }
}

@Preview(showBackground = true, name = "Home – Empty state")
@Composable
private fun HomeScreenEmptyPreview() {
    MaterialTheme {
        HomeScreenContent(uiState = HomeUiState(userName = "Mr. Aiden Smith", isLoading = false))
    }
}