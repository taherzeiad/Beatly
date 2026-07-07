package com.beatly.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
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
import androidx.compose.ui.unit.sp
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
import com.taher.beatly.ui.theme.Gray100
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray600
import com.taher.beatly.ui.theme.Gray950
import com.taher.beatly.ui.theme.Purple500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onGetPremium: () -> Unit,
    onShareProfile: () -> Unit,
    onEditProfile: () -> Unit,
    onSettingClicked: (String) -> Unit,
    onNavigateTab: (BeatlyTab) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Dialogs state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { showDeleteDialog = false; viewModel.onDeleteAccount() }
        )
    }
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = { showLogoutDialog = false; viewModel.onLogout() }
        )
    }

    Scaffold(
        bottomBar = {
            BeatlyBottomBar(
                selectedTab = BeatlyTab.PROFILE,
                onTabSelected = onNavigateTab
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(White)
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoundIconButton(Icons.Default.ArrowBackIosNew, onBackClicked, "Back")
                Text(
                    "Profile",
                    style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextBlack
                )
                RoundIconButton(Icons.Default.MoreHoriz, {}, "More")
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Avatar + name ──────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    PlaceholderImage(
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape,
                        showLabel = false
                    )
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .align(Alignment.BottomEnd)
                            .clip(CircleShape)
                            .background(Purple500),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        uiState.userName,
                        style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                        color = TextBlack
                    )
                    Text(uiState.email, style = BodySmallRegular, color = Gray500)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Share / Edit buttons ───────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listOf(
                    "Share Profile" to onShareProfile,
                    "Edit Profile" to onEditProfile
                ).forEach { (label, action) ->
                    OutlinedButton(
                        onClick = action,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 1f),
                            contentColor = TextBlack
                        )
                    ) {
                        Text(label, style = BodySmallRegular)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Premium banner ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color(0xFF6B4EFF),
                                Color(0xFF9B7FFF)
                            )
                        )
                    )
                    .height(180.dp)
                    .width(335.dp)
            ) {
                Column(modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(20.dp)) {
                    Text(
                        "Enjoy All Benefits!",
                        fontSize = 20.sp,
                        style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                        color = White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Enjoy listening songs & \n podcasts with better audio \n quality, without restrictions, and \n without ads.",
                        style = BodyXSmallRegular, color = White.copy(alpha = 0.85f),
                        modifier = Modifier.widthIn(max = 200.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onGetPremium, modifier = Modifier.height(32.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = White),
                        contentPadding = PaddingValues(horizontal = 18.dp)
                    ) {
                        Text(
                            "Get Premium",
                            style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold),
                            color = Purple500,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Settings rows ──────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.settings.forEach { item ->
                    SettingRow(
                        item = item,
                        onClicked = {
                            when (item.id) {
                                "delete_account" -> showDeleteDialog = true
                                "logout" -> showLogoutDialog = true
                                else -> onSettingClicked(item.id)
                            }
                        },
                        onDarkModeToggle = viewModel::onDarkModeToggled
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// ── Setting row ────────────────────────────────────────────────────────────

@Composable
private fun SettingRow(
    item: ProfileSettingItem,
    onClicked: () -> Unit,
    onDarkModeToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { if (!item.isToggle) onClicked() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceFill),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (item.id) {
                "profile" -> Icons.Default.Person
                "notification" -> Icons.Default.Notifications
                "dark_mode" -> Icons.Default.Circle
                "audio_video" -> Icons.Default.Mic
                "playback" -> Icons.Default.PlayCircle
                "downloads" -> Icons.Default.Download
                "privacy_policy" -> Icons.Default.Lock
                "security" -> Icons.Default.Security
                "language" -> Icons.Default.Language
                "about" -> Icons.Default.Info
                "delete_account" -> Icons.Default.DeleteForever
                "logout" -> Icons.AutoMirrored.Filled.Logout
                else -> Icons.Default.ChevronRight
            }
            Icon(icon, null, tint = Gray600, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                item.label,
                style = BodySmallRegular,
                color = TextBlack,
                modifier = Modifier.weight(1f)
            )
            if (item.isToggle) {
                Switch(
                    checked = item.toggled, onCheckedChange = { onDarkModeToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = White, checkedTrackColor = Purple500,
                        uncheckedThumbColor = White, uncheckedTrackColor = Gray200
                    )
                )
            } else {
                Icon(
                    Icons.Default.ChevronRight,
                    null,
                    tint = Gray400,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Dialogs ────────────────────────────────────────────────────────────────

@Composable
fun DeleteAccountDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                "Delete Account", style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                color = TextBlack
            )
        },
        text = {
            Text(
                "Are you sure want to delete this EchoBeat account from your system?",
                style = BodySmallRegular, color = Gray500
            )
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss, shape = RoundedCornerShape(50),
                border = androidx.compose.foundation.BorderStroke(1.dp, Gray200),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Cancel", style = BodySmallRegular, color = TextBlack)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm, shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Purple500),
                modifier = Modifier.height(44.dp)
            ) {
                Text("Delete", style = BodySmallRegular, color = White)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogoutDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, containerColor = White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Logout",
                style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.Red
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Gray100)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Are you sure you want to log out?", style = BodySmallRegular, color = TextBlack)
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onDismiss, modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
                ) {
                    Text("Cancel", style = BodySmallRegular, color = TextBlack)
                }
                Button(
                    onClick = onConfirm, modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500)
                ) {
                    Text("Yes, Logout", style = BodySmallRegular, color = White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProfileBottomSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss, containerColor = White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, null, tint = TextBlack)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "Share Profile",
                    style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextBlack
                )
                Spacer(modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            // QR code placeholder
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .background(Gray100, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCode2, null, tint = Gray950, modifier = Modifier.size(180.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Direct Link",
                style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold),
                color = TextBlack,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(SurfaceFill)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        "lstr-2458Jenny Wilson profil...",
                        style = BodySmallRegular,
                        color = Gray500,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {}, shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text("Copy", style = BodySmallRegular, color = White)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    BeatlyTheme {
        ProfileScreen(
            onBackClicked = {},
            onGetPremium = {},
            onShareProfile = {},
            onEditProfile = {},
            onSettingClicked = {},
            onNavigateTab = {}
        )
    }
}