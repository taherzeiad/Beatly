package com.taher.beatly.ui.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.model.Artist
import com.taher.beatly.model.SearchFilter
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.theme.SurfaceFill

@Composable
fun SearchArtistsScreen(
    onBackClick   : () -> Unit,
    onArtistClick : (String) -> Unit,
    onSongClick   : (com.taher.beatly.model.Song) -> Unit,
    initialQuery  : String? = null,
    viewModel     : SearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    androidx.compose.runtime.LaunchedEffect(initialQuery) {
        if (initialQuery != null) {
            viewModel.onQueryChanged(initialQuery)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                RoundIconButton(
                    icon               = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick            = onBackClick,
                    contentDescription = "Back"
                )
                Spacer(Modifier.width(12.dp))
                SearchField(
                    query = uiState.query,
                    onQueryChanged = viewModel::onQueryChanged,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(16.dp))
            FilterChipsRow(
                selected = uiState.selectedFilter,
                onFilterSelected = viewModel::onFilterSelected
            )

            Spacer(Modifier.height(16.dp))
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    when (uiState.selectedFilter) {
                        SearchFilter.SONGS -> {
                            items(uiState.songs, key = { it.id }) { song ->
                                com.taher.beatly.ui.components.SongRow(
                                    song = song,
                                    onLikeClick = { viewModel.onLikeToggled(song.id) },
                                    onPlayClick = {
                                        viewModel.onPlaySong(song)
                                        onSongClick(song)
                                    }
                                )
                            }
                        }
                        SearchFilter.ALBUMS -> {
                            items(uiState.albums, key = { it.id }) { album ->
                                AlbumSearchRow(album = album, onClick = { /* Navigate to album detail */ })
                            }
                        }
                        SearchFilter.PLAYLISTS -> {
                            items(uiState.playlists, key = { it.id }) { playlist ->
                                PlaylistSearchRow(playlist = playlist, onClick = { /* Navigate to playlist detail */ })
                            }
                        }
                        else -> {
                            items(uiState.artists, key = { it.id }) { artist ->
                                ArtistSearchRow(
                                    artist = artist,
                                    onArtistClick = { onArtistClick(artist.id) },
                                    onFollowClick = { viewModel.onFollowToggled(artist.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AlbumSearchRow(album: com.taher.beatly.model.Album, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        com.taher.beatly.ui.components.BeatlyImage(
            url = album.imageUrl,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(album.name, style = MaterialTheme.typography.labelLarge)
            Text(album.artistName, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun PlaylistSearchRow(playlist: com.taher.beatly.model.Playlist, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        com.taher.beatly.ui.components.BeatlyImage(
            url = playlist.imageUrl,
            modifier = Modifier.size(56.dp)
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(playlist.name, style = MaterialTheme.typography.labelLarge)
            Text("By ${playlist.ownerName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = modifier,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        placeholder = { Text("Search artists, songs...") },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Gray200
        )
    )
}

@Composable
private fun FilterChipsRow(selected: SearchFilter, onFilterSelected: (SearchFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        val labels = listOf(
            "Top" to SearchFilter.TOP,
            "Songs" to SearchFilter.SONGS,
            "Artists" to SearchFilter.ARTISTS,
            "Albums" to SearchFilter.ALBUMS,
            "Playlists" to SearchFilter.PLAYLISTS
        )
        labels.forEach { (label, filter) ->
            FilterChip(
                selected = selected == filter,
                onClick = { onFilterSelected(filter) },
                label = { Text(label) },
                shape = RoundedCornerShape(50),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.background,
                    containerColor = SurfaceFill
                )
            )
        }
    }
}

@Composable
private fun ArtistSearchRow(artist: Artist, onArtistClick: () -> Unit, onFollowClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onArtistClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        com.taher.beatly.ui.components.BeatlyImage(
            url = artist.imageUrl,
            modifier = Modifier.size(56.dp),
            shape = CircleShape
        )
        Spacer(Modifier.width(12.dp))
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(artist.name, style = MaterialTheme.typography.labelLarge)
            if (artist.isVerified) {
                Spacer(Modifier.width(6.dp))
                Icon(
                    Icons.Filled.Verified,
                    contentDescription = "Verified",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        FollowButton(isFollowing = artist.isFollowing, onClick = onFollowClick)
    }
}

@Composable
private fun FollowButton(isFollowing: Boolean, onClick: () -> Unit) {
    if (isFollowing) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
        ) { Text("Following") }
    } else {
        Button(
            onClick = onClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) { Text("Follow", color = MaterialTheme.colorScheme.background) }
    }
}