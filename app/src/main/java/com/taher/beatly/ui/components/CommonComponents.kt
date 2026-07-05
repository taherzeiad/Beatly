package com.taher.beatly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray950
import com.taher.beatly.model.Song

/** Placeholder image box, used everywhere real artwork is unavailable. */
@Composable
fun PlaceholderImage(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp),
    showLabel: Boolean = true
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.outline, shape)
            .clip(shape),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Outlined.Image,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(36.dp)
            )
            if (showLabel) {
                Spacer(Modifier.height(4.dp))
                Text(
                    "No Image Preview",
                    color = Gray400,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

enum class BeatlyTab { HOME, EXPLORE, LIBRARY, PROFILE }

@Composable
fun BeatlyBottomBar(
    selectedTab: BeatlyTab,
    onTabSelected: (BeatlyTab) -> Unit
) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
        NavigationBarItem(
            selected = selectedTab == BeatlyTab.HOME,
            onClick = { onTabSelected(BeatlyTab.HOME) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            colors = navBarColors()
        )
        NavigationBarItem(
            selected = selectedTab == BeatlyTab.EXPLORE,
            onClick = { onTabSelected(BeatlyTab.EXPLORE) },
            icon = { Icon(Icons.Outlined.Explore, contentDescription = "Explore") },
            label = { Text("Explore") },
            colors = navBarColors()
        )
        NavigationBarItem(
            selected = selectedTab == BeatlyTab.LIBRARY,
            onClick = { onTabSelected(BeatlyTab.LIBRARY) },
            icon = { Icon(Icons.Outlined.LibraryBooks, contentDescription = "Library") },
            label = { Text("Library") },
            colors = navBarColors()
        )
        NavigationBarItem(
            selected = selectedTab == BeatlyTab.PROFILE,
            onClick = { onTabSelected(BeatlyTab.PROFILE) },
            icon = { Icon(Icons.Outlined.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            colors = navBarColors()
        )
    }
}

@Composable
private fun navBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = MaterialTheme.colorScheme.primary,
    selectedTextColor = MaterialTheme.colorScheme.primary,
    unselectedIconColor = Gray400,
    unselectedTextColor = Gray400,
    indicatorColor = Color.Transparent
)

/** Horizontal "Section Title ... See All" row used on Home and elsewhere. */
@Composable
fun SectionHeader(title: String, onSeeAllClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 18.sp, style = MaterialTheme.typography.titleMedium)
        if (onSeeAllClick != null) {
            Text(
                "See All",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onSeeAllClick)
            )
        }
    }
}

/** A song row with like / save / play controls, used in trending lists, liked songs, artist pages. */
@Composable
fun SongRow(
    song: Song,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit = {},
    onPlayClick: () -> Unit,
    isCurrentlyPlaying: Boolean = false,
    onPauseClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlaceholderImage(modifier = Modifier.width(115.dp).height(130.dp), shape = RoundedCornerShape(12.dp))
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                song.title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                fontSize = 15.sp,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                song.artistName,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onLikeClick, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = if (song.isLiked) Icons.Filled.CheckCircle else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (song.isLiked) MaterialTheme.colorScheme.primary else Gray500
                    )
                }
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onSaveClick, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.BookmarkBorder, contentDescription = "Save", tint = Gray500)
                }
            }
        }
        if (isCurrentlyPlaying) {
            Button(
                onClick = onPauseClick,
                colors = ButtonDefaults.buttonColors(containerColor = Gray500),
                shape = RoundedCornerShape(50)
            ) {
                Icon(Icons.Filled.Pause, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Pause")
            }
        } else {
            Button(
                onClick = onPlayClick,
                colors = ButtonDefaults.buttonColors(containerColor = Gray500),
                shape = RoundedCornerShape(50)
            ) {
                Icon(
                    Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text("Play")
            }
        }
    }
}

@Composable
fun BeatlyTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionIcon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Filled.Search
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundIconButton(
            icon = Icons.Filled.ArrowBack,
            onClick = { onBackClick?.invoke() },
            contentDescription = "Back"
        )
        Text(title, style = MaterialTheme.typography.titleMedium)
        RoundIconButton(
            icon = actionIcon,
            onClick = { onActionClick?.invoke() },
            contentDescription = "Action"
        )
    }
}

@Composable
fun RoundIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    contentDescription: String?
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = contentDescription, tint = Gray950)
    }
}
