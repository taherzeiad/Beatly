package com.taher.beatly.ui.genre

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.model.Genre
import com.taher.beatly.ui.components.BeatlyTopBar

@Composable
fun AllGenreScreen(
    onBackClick   : () -> Unit,
    onSearchClick : () -> Unit,
    onGenreClick  : (String) -> Unit,
    viewModel     : GenreViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            BeatlyTopBar(title = "All Genre", onBackClick = onBackClick, onActionClick = onSearchClick)
            Spacer(Modifier.height(20.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(uiState.genres, key = { it.id }) { genre ->
                    GenreCard(
                        genre   = genre,
                        onClick = { onGenreClick(genre.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun GenreCard(genre: Genre, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.outline)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = genre.name,
            color = MaterialTheme.colorScheme.background,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(16.dp)
        )
    }
}
