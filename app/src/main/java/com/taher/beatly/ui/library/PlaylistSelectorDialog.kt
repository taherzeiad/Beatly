package com.taher.beatly.ui.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.domain.model.Playlist
import com.taher.beatly.ui.components.BeatlyImage

@Composable
fun PlaylistSelectorDialog(
    onDismiss: () -> Unit,
    onPlaylistSelected: (Playlist) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxWidth().heightIn(max = 500.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add to playlist", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(16.dp))
                
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(uiState.items.filter { it.id != "liked_songs" && it.id != "followed_artists" }) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { 
                                    onPlaylistSelected(Playlist(id = item.id, name = item.name, ownerId = "")) 
                                    onDismiss()
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BeatlyImage(url = null, modifier = Modifier.size(48.dp), shape = RoundedCornerShape(8.dp))
                            Spacer(Modifier.width(12.dp))
                            Text(item.name, style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                }
                
                if (uiState.items.none { it.id != "liked_songs" && it.id != "followed_artists" }) {
                    Text("No playlists found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                    Text("Cancel")
                }
            }
        }
    }
}
