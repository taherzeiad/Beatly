package com.taher.beatly.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.model.LibraryFilter
import com.taher.beatly.model.LibraryItem
import com.taher.beatly.model.LibraryItemIcon
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.theme.SurfaceFill

/**
 * Stateful entry point wired to Hilt + the real ViewModel.
 * This is what the NavGraph calls.
 */
@Composable
fun MyLibraryScreen(
    onBackClick: () -> Unit,
    onLibraryItemClick: (LibraryItem) -> Unit,
    viewModel: LibraryViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyLibraryContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onLibraryItemClick = onLibraryItemClick,
        onAddClicked = viewModel::onAddClicked,
        onSearchQueryChanged = viewModel::onSearchQueryChanged,
        onFilterSelected = viewModel::onFilterSelected,
        onToggleSortOrder = viewModel::onToggleSortOrder,
        onNewLibraryNameChanged = viewModel::onNewLibraryNameChanged,
        onDialogDismiss = viewModel::onDialogDismiss,
        onCreateLibraryConfirmed = viewModel::onCreateLibraryConfirmed
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
fun MyLibraryContent(
    uiState: LibraryUiState,
    onBackClick: () -> Unit = {},
    onLibraryItemClick: (LibraryItem) -> Unit = {},
    onAddClicked: () -> Unit = {},
    onSearchQueryChanged: (String) -> Unit = {},
    onFilterSelected: (LibraryFilter) -> Unit = {},
    onToggleSortOrder: () -> Unit = {},
    onNewLibraryNameChanged: (String) -> Unit = {},
    onDialogDismiss: () -> Unit = {},
    onCreateLibraryConfirmed: () -> Unit = {}
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RoundIconButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = onBackClick,
                    contentDescription = "Back"
                )
                Text("My Library", style = MaterialTheme.typography.titleLarge)
                RoundIconButton(
                    icon = Icons.Filled.Add,
                    onClick = onAddClicked,
                    contentDescription = "Create playlist"
                )
            }

            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = { Text("Search...") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Gray200
                )
            )

            Spacer(Modifier.height(12.dp))
            LibraryFilterChips(
                selected = uiState.selectedFilter,
                onFilterSelected = onFilterSelected
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggleSortOrder),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SwapVert, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text(if (uiState.isAscending) "Sort Ascending" else "Sort Descending")
                }
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = "List view")
            }

            Spacer(Modifier.height(8.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items(uiState.items, key = { it.id }) { item ->
                    LibraryItemRow(
                        item = item,
                        onClick = { onLibraryItemClick(item) }
                    )
                }
            }
        }
    }

    if (uiState.isCreateDialogVisible) {
        CreateLibraryDialog(
            name = uiState.newLibraryName,
            onNameChanged = onNewLibraryNameChanged,
            onDismiss = onDialogDismiss,
            onConfirm = onCreateLibraryConfirmed
        )
    }
}

@Composable
private fun LibraryFilterChips(selected: LibraryFilter, onFilterSelected: (LibraryFilter) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        val labels = listOf(
            "Songs" to LibraryFilter.SONGS,
            "Playlist" to LibraryFilter.PLAYLIST,
            "Albums" to LibraryFilter.ALBUMS,
            "Artist" to LibraryFilter.ARTIST
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
private fun LibraryItemRow(item: LibraryItem, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LibraryItemIcon(icon = item.icon)
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.name,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // تم دعم السكرول الأفقي هنا في حال كانت الشاشة ضيقة جداً كي لا ينقسم السطر
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val artistLabel = if (item.artistCount > 1) "Artists" else "Artist"
                Text(
                    text = "${item.songCount} songs • ${item.artistCount} $artistLabel",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
            }
        }
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun LibraryItemIcon(icon: LibraryItemIcon) {
    when (icon) {
        LibraryItemIcon.LIKED_SONGS -> IconBadge(Icons.Filled.Favorite)
        LibraryItemIcon.FOLLOWED_ARTISTS -> IconBadge(Icons.Filled.Groups)
        LibraryItemIcon.PLAYLIST -> IconBadge(Icons.AutoMirrored.Filled.QueueMusic)
        LibraryItemIcon.CUSTOM_IMAGE -> PlaceholderImage(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(12.dp),
            showLabel = false
        )
    }
}

@Composable
private fun IconBadge(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

// =========================================================================
// Previews
// =========================================================================

private val previewLibraryItems = listOf(
    LibraryItem(
        id = "1",
        name = "Liked Songs",
        icon = LibraryItemIcon.LIKED_SONGS,
        songCount = 120,
        artistCount = 32
    ),
    LibraryItem(
        id = "2",
        name = "Workout Beats",
        icon = LibraryItemIcon.PLAYLIST,
        songCount = 45,
        artistCount = 1
    ),
    LibraryItem(
        id = "3",
        name = "Rock Classics",
        icon = LibraryItemIcon.CUSTOM_IMAGE,
        songCount = 88,
        artistCount = 15
    )
)

private val previewLibraryUiState = LibraryUiState(
    searchQuery = "",
    selectedFilter = LibraryFilter.SONGS,
    isAscending = true,
    items = previewLibraryItems,
    isCreateDialogVisible = false,
    newLibraryName = ""
)

@Preview(showBackground = true, name = "Library – Light")
@Composable
private fun MyLibraryScreenPreview() {
    MaterialTheme {
        MyLibraryContent(uiState = previewLibraryUiState)
    }
}

@Preview(
    showBackground = true,
    name = "Library – Dark",
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun MyLibraryScreenPreviewDark() {
    MaterialTheme {
        MyLibraryContent(uiState = previewLibraryUiState)
    }
}

@Preview(showBackground = true, name = "Library – Empty state")
@Composable
private fun MyLibraryScreenEmptyPreview() {
    MaterialTheme {
        MyLibraryContent(uiState = LibraryUiState(items = emptyList()))
    }
}