package com.taher.beatly.ui.artist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton
import com.taher.beatly.ui.components.SectionHeader
import com.taher.beatly.ui.components.SongRow

@Composable
fun ArtistDetailScreen(
    onBackClick        : () -> Unit,
    onSeeAllSongsClick : () -> Unit,
    viewModel          : ArtistDetailViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val artist = uiState.artist ?: return

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
        ) {
            Spacer(Modifier.height(12.dp))

            // 🛠️ تعديل الشريط العلوي
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // زر الرجوع القياسي النظيف والحر من أي إضافات
                androidx.compose.material3.IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text("Artist", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.size(44.dp)) // للحفاظ على التوازن الهندسي للنص
            }

            Spacer(Modifier.height(20.dp))
            PlaceholderImage(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(220.dp),
                shape = CircleShape,
                showLabel = true
            )

            Spacer(Modifier.height(16.dp))
            Text(
                artist.name,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                "${"%,d".format(artist.monthlyListeners)} monthly listeners",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = viewModel::onFollowClick,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (artist.isFollowing) Gray200 else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        if (artist.isFollowing) "Following" else "Follow",
                        color = if (artist.isFollowing) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.background
                    )
                }

                Spacer(Modifier.width(12.dp))

                // 🛠️ تعديل زر الثلاث نقاط هنا لإضافة البوردر الأسود الصغير حوله فقط
                RoundIconButton(
                    icon = Icons.Filled.MoreVert,
                    onClick = { /* أكشن الخيارات هنا */ },
                    contentDescription = "More",
                    hasBorder = true // تفعيل البوردر الصغير المخصص
                )

                Spacer(Modifier.weight(1f))

                RoundIconButton(
                    icon = Icons.Filled.PlayArrow,
                    onClick = {
                        artist.popularSongs.firstOrNull()?.let { song ->
                            viewModel.onPlaySong(song)
                        }
                    },
                    contentDescription = "Play all",
                    hasBorder = false // زر التشغيل يبقى بدون بوردر خارجي
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            SectionHeader(title = "Popular Songs", onSeeAllClick = onSeeAllSongsClick)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                artist.popularSongs.forEach { song ->
                    SongRow(
                        song = song,
                        onLikeClick = { viewModel.onLikeSongToggled(song.id) },
                        onPlayClick = { viewModel.onPlaySong(song) }
                    )
                }
            }
        }
    }
}