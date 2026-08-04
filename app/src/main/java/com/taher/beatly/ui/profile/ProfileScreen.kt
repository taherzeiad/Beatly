package com.taher.beatly.ui.profile

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
import androidx.compose.ui.res.stringResource
import com.taher.beatly.R
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
    viewModel: ProfileViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onGetPremium: () -> Unit,
    onShareProfile: () -> Unit,
    onEditProfile: () -> Unit,
    onSettingClicked: (String) -> Unit,
    onNavigateTab: (BeatlyTab) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Handle logout navigation
    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) {
            onBackClicked() // Or specific logout action
        }
    }

    ProfileContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onGetPremium = onGetPremium,
        onShareProfile = onShareProfile,
        onEditProfile = onEditProfile,
        onSettingClicked = onSettingClicked,
        onNavigateTab = onNavigateTab,
        onDeleteAccount = viewModel::onDeleteAccount,
        onLogout = viewModel::onLogout,
        onDarkModeToggle = viewModel::onDarkModeToggled
    )
}

@Composable
fun ProfileContent(
    uiState: ProfileUiState,
    onBackClicked: () -> Unit,
    onGetPremium: () -> Unit,
    onShareProfile: () -> Unit,
    onEditProfile: () -> Unit,
    onSettingClicked: (String) -> Unit,
    onNavigateTab: (BeatlyTab) -> Unit,
    onDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    onDarkModeToggle: () -> Unit
) {
    // Dialogs state
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showShareSheet by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAccountDialog(
            onDismiss = { showDeleteDialog = false },
            onConfirm = { showDeleteDialog = false; onDeleteAccount() }
        )
    }
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = { showLogoutDialog = false; onLogout() }
        )
    }
    if (showShareSheet) {
        ShareProfileBottomSheet(
            onDismiss = { showShareSheet = false },
            shareLink = uiState.shareLink
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
                RoundIconButton(Icons.Default.ArrowBackIosNew, onBackClicked, stringResource(R.string.cancel))
                Text(
                    stringResource(R.string.profile),
                    style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                    color = TextBlack
                )
                RoundIconButton(Icons.Default.MoreHoriz, { showShareSheet = true }, "More", true)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Avatar + name ──────────────────────────────────────────────
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box {
                    com.taher.beatly.ui.components.BeatlyImage(
                        url = uiState.avatarUrl,
                        modifier = Modifier.size(72.dp),
                        shape = CircleShape
                    )
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .align(Alignment.BottomEnd)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Purple500)
                            .clickable { onEditProfile() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = White,
                            modifier = Modifier.size(15.dp)
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

            // ── Premium banner ─────────────────────────────────────────────
            if (!uiState.isPremium) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFF424242)) // Grey-ish background as per Image 20
                        .height(180.dp)
                ) {
                    // Pattern / Image background could be added here
                    Column(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(20.dp)
                    ) {
                        Text(
                            stringResource(R.string.premium_benefits_title),
                            fontSize = 20.sp,
                            style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                            color = White
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            stringResource(R.string.premium_benefits_desc),
                            style = BodyXSmallRegular, color = White.copy(alpha = 0.85f),
                            modifier = Modifier.widthIn(max = 240.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onGetPremium, modifier = Modifier.height(36.dp),
                            shape = RoundedCornerShape(50),
                            colors = ButtonDefaults.buttonColors(containerColor = White),
                            contentPadding = PaddingValues(horizontal = 18.dp)
                        ) {
                            Text(
                                stringResource(R.string.get_premium),
                                style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold),
                                color = Purple500,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.horizontalGradient(listOf(Purple500, Color(0xFF9C27B0))))
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Star, null, tint = White, modifier = Modifier.size(32.dp))
                        Text(
                            stringResource(R.string.premium_member_status),
                            style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
                            color = White
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
                        onDarkModeToggle = onDarkModeToggle
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
            val label = when (item.id) {
                "profile" -> stringResource(R.string.edit_profile)
                "notification" -> stringResource(R.string.notification)
                "dark_mode" -> stringResource(R.string.dark_mode)
                "audio_video" -> stringResource(R.string.audio_video)
                "playback" -> stringResource(R.string.playback)
                "downloads" -> stringResource(R.string.data_saver)
                "privacy_policy" -> stringResource(R.string.privacy_policy)
                "security" -> stringResource(R.string.security)
                "language" -> stringResource(R.string.language)
                "about" -> stringResource(R.string.about)
                "delete_account" -> stringResource(R.string.delete_account)
                "logout" -> stringResource(R.string.logout)
                else -> item.label
            }
            Text(
                label,
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
                stringResource(R.string.delete_account), style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
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
                Text(stringResource(R.string.cancel), style = BodySmallRegular, color = TextBlack)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm, shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Purple500),
                modifier = Modifier.height(44.dp)
            ) {
                Text(stringResource(R.string.delete), style = BodySmallRegular, color = White)
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
                stringResource(R.string.logout),
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
                    Text(stringResource(R.string.cancel), style = BodySmallRegular, color = TextBlack)
                }
                Button(
                    onClick = onConfirm, modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500)
                ) {
                    Text(stringResource(R.string.yes_logout), style = BodySmallRegular, color = White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProfileBottomSheet(onDismiss: () -> Unit, shareLink: String) {
    val clipboardManager = androidx.compose.ui.platform.LocalClipboardManager.current

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
                    stringResource(R.string.share_profile),
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
                stringResource(R.string.direct_link),
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
                        shareLink,
                        style = BodySmallRegular,
                        color = Gray500,
                        maxLines = 1
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(shareLink))
                    }, shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple500),
                    modifier = Modifier.height(44.dp)
                ) {
                    Text(stringResource(R.string.copy), style = BodySmallRegular, color = White)
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, apiLevel = 35)
@Composable
fun ProfileScreenPreview() {
    BeatlyTheme {
        ProfileContent(
            uiState = ProfileUiState(
                userName = "Jenny Wilson",
                email = "jenny.wilson@example.com",
                settings = listOf(
                    ProfileSettingItem("profile", "Profile"),
                    ProfileSettingItem("notification", "Notification"),
                    ProfileSettingItem("dark_mode", "Dark Mode", isToggle = true, toggled = false),
                    ProfileSettingItem("audio_video", "Audio & Video"),
                    ProfileSettingItem("playback", "Playback"),
                    ProfileSettingItem("downloads", "Data Saver & Storage"),
                    ProfileSettingItem("security", "Security"),
                    ProfileSettingItem("language", "Language"),
                    ProfileSettingItem("privacy_policy", "Privacy Policy"),
                    ProfileSettingItem("about", "About"),
                    ProfileSettingItem("delete_account", "Delete Account"),
                    ProfileSettingItem("logout", "Logout"),
                )
            ),
            onBackClicked = {},
            onGetPremium = {},
            onShareProfile = {},
            onEditProfile = {},
            onSettingClicked = {},
            onNavigateTab = {},
            onDeleteAccount = {},
            onLogout = {},
            onDarkModeToggle = {}
        )
    }
}