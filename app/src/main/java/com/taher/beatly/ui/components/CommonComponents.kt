package com.taher.beatly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.LibraryBooks
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray950
import com.taher.beatly.model.Song

import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage

/** Placeholder image box, used everywhere real artwork is unavailable. */
@Composable
fun PlaceholderImage(
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp),
    showLabel: Boolean = true,
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

@Composable
fun BeatlyImage(
    url: String?,
    modifier: Modifier = Modifier,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(16.dp),
    contentDescription: String? = null,
    contentScale: ContentScale = ContentScale.Crop
) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.outline, shape),
        contentScale = contentScale,
        loading = { PlaceholderImage(modifier = Modifier.fillMaxSize(), shape = shape) },
        error = { PlaceholderImage(modifier = Modifier.fillMaxSize(), shape = shape) }
    )
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
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.LibraryBooks,
                    contentDescription = "Library"
                )
            },
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
                text     = "See All",
                color    = MaterialTheme.colorScheme.primary,
                style    = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable(onClick = onSeeAllClick),
            )
        }
    }
}

/** A song row with like / save / play controls, used in trending lists, liked songs, artist pages. */
@Composable
fun SongRow(
    song: Song,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSaveClick: () -> Unit = {},
    onPlayClick: () -> Unit,
    isCurrentlyPlaying: Boolean = false,
    onPauseClick: () -> Unit = {},
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BeatlyImage(
                url = song.imageUrl,
                modifier = Modifier.size(85.dp),
                shape = RoundedCornerShape(16.dp)
            )
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
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (song.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (song.isLiked) MaterialTheme.colorScheme.primary else Gray500,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onLikeClick() }
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Saved",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = Gray500,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onSaveClick() }
                    )
                }
            }

            Button(
                onClick = if (isCurrentlyPlaying) onPauseClick else onPlayClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCurrentlyPlaying) Gray500 else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = if (isCurrentlyPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(if (isCurrentlyPlaying) "Pause" else "Play", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun BeatlyTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    onActionClick: (() -> Unit)? = null,
    actionIcon: ImageVector = Icons.Filled.Search,
    actionHasBorder: Boolean = false // 🛠️ باراميتر مضاف للتحكم في بوردر الأكشن الجانبي (مثل النقاط الثلاث)
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (onBackClick != null) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Gray950
                )
            }
        } else {
            Spacer(Modifier.size(44.dp))
        }

        Text(title, style = MaterialTheme.typography.titleMedium)

        if (onActionClick != null) {
            RoundIconButton(
                icon = actionIcon,
                onClick = onActionClick,
                contentDescription = "Action",
                hasBorder = actionHasBorder // يمرر القيمة المحددة للبوردر
            )
        } else {
            Spacer(Modifier.size(44.dp))
        }
    }
}

@Composable
fun RoundIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    contentDescription: String?,
    hasBorder: Boolean = false
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (hasBorder) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .border(width = 1.dp, color = Color.Black, shape = CircleShape)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = contentDescription,
                    tint = Gray950,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Icon(icon, contentDescription = contentDescription, tint = Gray950)
        }
    }
}