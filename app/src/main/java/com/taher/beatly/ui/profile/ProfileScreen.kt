package com.taher.beatly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taher.beatly.ui.components.BeatlyBottomBar
import com.taher.beatly.ui.components.BeatlyTab
import com.taher.beatly.ui.components.PlaceholderImage
import com.taher.beatly.ui.components.RoundIconButton
import com.taher.beatly.ui.theme.BeatlyTheme
import com.taher.beatly.ui.theme.BodyMediumMedium
import com.taher.beatly.ui.theme.BodySmallRegular
import com.taher.beatly.ui.theme.BodyXSmallRegular
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray600
import com.taher.beatly.ui.theme.Purple500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    viewModel       : ProfileViewModel = viewModel(),
    onBackClicked   : () -> Unit,
    onGetPremium    : () -> Unit,
    onSettingClicked: (String) -> Unit,
    onNavigateTab   : (BeatlyTab) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = { BeatlyBottomBar(selectedTab = BeatlyTab.PROFILE, onTabSelected = onNavigateTab) }
    ) { padding ->
        ProfileContent(
            uiState          = uiState,
            modifier         = Modifier.padding(padding),
            onBackClicked    = onBackClicked,
            onGetPremium     = onGetPremium,
            onSettingClicked = onSettingClicked,
            onDarkModeToggle = viewModel::onDarkModeToggled
        )
    }
}

// ── Stateless content ──────────────────────────────────────────────────────

@Composable
private fun ProfileContent(
    uiState          : ProfileUiState,
    modifier         : Modifier = Modifier,
    onBackClicked    : () -> Unit,
    onGetPremium     : () -> Unit,
    onSettingClicked : (String) -> Unit,
    onDarkModeToggle : () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(White)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Top bar ────────────────────────────────────────────────────────
        ProfileTopBar(
            onBackClicked = onBackClicked,
            onMoreClicked = {}
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── User info ──────────────────────────────────────────────────────
        Row(
            modifier          = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box {
                PlaceholderImage(
                    modifier  = Modifier.size(72.dp),
                    shape     = CircleShape,
                    showLabel = false
                )
                // Edit badge
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(Purple500),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, tint = White, modifier = Modifier.size(12.dp))
                }
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(text = uiState.userName, style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold), color = TextBlack)
                Text(text = uiState.email,    style = BodySmallRegular,  color = Gray500)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Premium banner ─────────────────────────────────────────────────
        PremiumBanner(onGetPremium = onGetPremium)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Settings list ──────────────────────────────────────────────────
        Column(
            modifier            = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            uiState.settings.forEach { item ->
                SettingRow(
                    item            = item,
                    onClicked       = { onSettingClicked(item.id) },
                    onDarkModeToggle = onDarkModeToggle
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ── Sub-composables ────────────────────────────────────────────────────────

@Composable
private fun ProfileTopBar(onBackClicked: () -> Unit, onMoreClicked: () -> Unit) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RoundIconButton(
            icon               = Icons.Default.ArrowBackIosNew,
            onClick            = onBackClicked,
            contentDescription = "Back")
        Text(text = "Profile", style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold), color = TextBlack)
        RoundIconButton(
            icon               = Icons.Default.MoreHoriz,
            onClick            = onMoreClicked,
            contentDescription = "More"
        )
    }
}

@Composable
private fun PremiumBanner(onGetPremium: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(listOf(Color(0xFF6B4EFF), Color(0xFF9B7FFF)))
            )
            .height(140.dp)
    ) {
        // Background faint image placeholder
        PlaceholderImage(
            modifier  = Modifier
                .size(110.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            showLabel = false
        )

        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(20.dp)
        ) {
            Text("Enjoy All Benefits!",  style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold), color = White)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                "Enjoy listening songs & podcasts with better audio quality, without restrictions, and without ads.",
                style   = BodyXSmallRegular,
                color   = White.copy(alpha = 0.85f),
                modifier = Modifier.widthIn(max = 200.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick  = onGetPremium,
                modifier = Modifier.height(34.dp),
                shape    = RoundedCornerShape(50),
                colors   = ButtonDefaults.buttonColors(containerColor = White),
                contentPadding = PaddingValues(horizontal = 18.dp)
            ) {
                Text("Get Premium", style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold), color = Purple500)
            }
        }
    }
}

@Composable
private fun SettingRow(
    item            : ProfileSettingItem,
    onClicked       : () -> Unit,
    onDarkModeToggle: () -> Unit
) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { if (!item.isToggle) onClicked() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon per setting
            val icon = when (item.id) {
                "profile"        -> Icons.Default.Person
                "notification"   -> Icons.Default.Notifications
                "dark_mode"      -> Icons.Default.Circle
                "audio_video"    -> Icons.Default.Mic
                "playback"       -> Icons.Default.PlayCircle
                "downloads"      -> Icons.Default.Download
                "privacy_policy" -> Icons.Default.Lock
                "about"          -> Icons.Default.Info
                "logout"         -> Icons.Default.Logout
                else             -> Icons.Default.ChevronRight
            }
            Icon(icon, contentDescription = null, tint = Gray600, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(text = item.label, style = BodySmallRegular, color = TextBlack, modifier = Modifier.weight(1f))

            if (item.isToggle) {
                Switch(
                    checked         = item.toggled,
                    onCheckedChange = { onDarkModeToggle() },
                    colors          = SwitchDefaults.colors(
                        checkedThumbColor   = White,
                        checkedTrackColor   = Purple500,
                        uncheckedThumbColor = White,
                        uncheckedTrackColor = Gray200
                    )
                )
            } else {
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Gray400, modifier = Modifier.size(20.dp))
            }
        }
    }
}

// ── Previews ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    BeatlyTheme {
        ProfileScreen(
            onBackClicked    = {},
            onGetPremium     = {},
            onSettingClicked = {},
            onNavigateTab    = {}
        )
    }
}