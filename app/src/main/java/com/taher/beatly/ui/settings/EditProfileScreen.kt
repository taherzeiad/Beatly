package com.taher.beatly.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taher.beatly.ui.components.AuthFieldLabel
import com.taher.beatly.ui.components.AuthTextField
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.theme.*

// ═════════════════════════════════════════════════════════════════════════════
// Edit Profile
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel    : EditProfileViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onUpdated    : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar("Edit Profile", onBackClicked)

            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Name
                Column {
                    AuthFieldLabel("Name")
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(uiState.name, viewModel::onNameChanged, "Name")
                }
                // Username
                Column {
                    AuthFieldLabel("Username")
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(uiState.username, viewModel::onUsernameChanged, "Username")
                }
                // Birth Date
                Column {
                    AuthFieldLabel("Birth Date")
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value         = uiState.birthDate,
                        onValueChange = viewModel::onBirthDateChanged,
                        modifier      = Modifier.fillMaxWidth().height(52.dp),
                        textStyle     = BodySmallRegular.copy(color = Gray950),
                        singleLine    = true,
                        shape         = RoundedCornerShape(12.dp),
                        colors        = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceFill, focusedContainerColor = SurfaceFill,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedBorderColor = Purple500
                        ),
                        trailingIcon  = { Icon(Icons.Default.CalendarMonth, null, tint = Gray400) }
                    )
                }
                // Mail
                Column {
                    AuthFieldLabel("Mail")
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(uiState.mail, viewModel::onMailChanged, "Mail", keyboardType = KeyboardType.Email)
                }
                // Gender dropdown
                Column {
                    AuthFieldLabel("Gender")
                    Spacer(Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value         = uiState.gender,
                            onValueChange = {},
                            modifier      = Modifier.fillMaxWidth().menuAnchor().height(52.dp),
                            readOnly      = true,
                            textStyle     = BodySmallRegular.copy(color = Gray950),
                            shape         = RoundedCornerShape(12.dp),
                            colors        = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceFill, focusedContainerColor = SurfaceFill,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedBorderColor = Purple500
                            ),
                            trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                        )
                        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf("Male","Female","Prefer not to say").forEach { option ->
                                DropdownMenuItem(
                                    text    = { Text(option, style = BodySmallRegular) },
                                    onClick = { viewModel.onGenderChanged(option); expanded = false }
                                )
                            }
                        }
                    }
                }
            }
        }
        AuthPrimaryButton("Update", onUpdated, modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 40.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Notification
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun NotificationScreen(
    viewModel    : NotificationViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onUpdated    : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 120.dp)) {
            SettingsTopBar("Notification", onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.items.forEach { item ->
                    SettingsValueRow(label = item.label, value = item.subtitle)
                }
            }
        }
        AuthPrimaryButton("Update", onUpdated, modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 40.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Audio & Video
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AudioVideoScreen(
    viewModel    : AudioVideoViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onUpdated    : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 120.dp)) {
            SettingsTopBar("Audio & Video", onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionLabel("Audio Quality")
                SettingsValueRow("Wi-Fi Streaming",       uiState.wifiStreamingAudio)
                SettingsValueRow("Data Cellular Streaming", uiState.cellularStreamingAudio)
                SettingsToggleRow("Auto Adjust Quality",  checked = uiState.autoAdjustQuality, onToggle = viewModel::onAutoAdjustToggled)
                SettingsValueRow("Download",              uiState.downloadQuality)

                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel("Video Quality")
                SettingsValueRow("Wi-Fi Streaming",       uiState.wifiStreamingVideo)
                SettingsValueRow("Data Cellular Streaming", uiState.cellularStreamingVideo)
            }
        }
        AuthPrimaryButton("Update", onUpdated, modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 40.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Playback
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun PlaybackScreen(
    viewModel    : PlaybackViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onUpdated    : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 120.dp)) {
            SettingsTopBar("Playback", onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.settings.forEach { s ->
                    SettingsToggleRow(label = s.label, subtitle = s.subtitle, checked = s.enabled, onToggle = { viewModel.onToggled(s.id) })
                }
            }
        }
        AuthPrimaryButton("Update", onUpdated, modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 40.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Data Saver & Storage
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun DataSaverScreen(
    viewModel    : DataSaverViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onUpdated    : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 120.dp)) {
            SettingsTopBar("Data Saver & Storage", onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionLabel("Data Saver")
                SettingsToggleRow("Audio Quality",
                    subtitle = "Sets your audio quality to low (24kbit/s) and disables artist canvases.",
                    checked  = uiState.audioQualitySaver,
                    onToggle = viewModel::onAudioQualityToggled)

                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel("Podcasts")
                SettingsToggleRow("Download Audio Only",  subtitle = "Save video podcasts as audio only.",
                    checked = uiState.downloadAudioOnly, onToggle = viewModel::onDownloadAudioToggled)
                SettingsToggleRow("Stream Audio Only",
                    subtitle = "Play video podcasts as audio only when not connected on Wi-Fi.",
                    checked  = uiState.streamAudioOnly, onToggle = viewModel::onStreamAudioToggled)

                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel("Storage")
                SettingsValueRow("Other Apps", uiState.otherAppsStorage)
                SettingsValueRow("Cache",      uiState.cacheStorage)
            }
        }
        AuthPrimaryButton("Update", onUpdated, modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 40.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Security
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun SecurityScreen(
    viewModel    : SecurityViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onChangePin  : () -> Unit,
    onChangePassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize().background(White).verticalScroll(rememberScrollState())) {
        SettingsTopBar("Security", onBackClicked)
        Spacer(Modifier.height(20.dp))
        Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SettingsToggleRow("Remember me",         checked = uiState.rememberMe,   onToggle = viewModel::onRememberMeToggled)
            SettingsToggleRow("Face ID",             checked = uiState.faceId,       onToggle = viewModel::onFaceIdToggled)
            SettingsToggleRow("Biometric ID",        checked = uiState.biometricId,  onToggle = viewModel::onBiometricToggled)
            SettingsValueRow("Google Authenticator", onClick = {})
            Spacer(Modifier.height(8.dp))
            AuthPrimaryButton("Change Pin",      onChangePin,      enabled = false)
            AuthPrimaryButton("Change Password", onChangePassword, enabled = false)
        }
        Spacer(Modifier.height(40.dp))
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Language
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun LanguageScreen(
    viewModel    : LanguageViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onChanged    : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(bottom = 120.dp)) {
            SettingsTopBar("Language", onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                uiState.groups.forEach { group ->
                    SettingsSectionLabel(group.groupTitle)
                    group.languages.forEach { lang ->
                        val isSelected = lang == uiState.selectedLang
                        Card(
                            onClick   = { viewModel.onLanguageSelected(lang) },
                            shape     = RoundedCornerShape(14.dp),
                            colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier  = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(lang, style = BodySmallRegular, color = TextBlack, modifier = Modifier.weight(1f))
                                Box(modifier = Modifier.size(20.dp),
                                    contentAlignment = Alignment.Center) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick  = { viewModel.onLanguageSelected(lang) },
                                        colors   = RadioButtonDefaults.colors(
                                            selectedColor   = Purple500,
                                            unselectedColor = Gray200
                                        )
                                    )
                                }
                            }
                        }
                    }
                    Spacer(Modifier.height(4.dp))
                }
            }
        }
        AuthPrimaryButton("Change", onChanged, modifier = Modifier.align(Alignment.BottomCenter).padding(horizontal = 20.dp, vertical = 40.dp))
    }
}

// ── Previews ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable fun EditProfilePreview()  { BeatlyTheme { EditProfileScreen(onBackClicked = {}, onUpdated = {}) } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun NotificationPreview() { BeatlyTheme { NotificationScreen(onBackClicked = {}, onUpdated = {}) } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun AudioVideoPreview()   { BeatlyTheme { AudioVideoScreen(onBackClicked = {}, onUpdated = {}) } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun PlaybackPreview()     { BeatlyTheme { PlaybackScreen(onBackClicked = {}, onUpdated = {}) } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun DataSaverPreview()    { BeatlyTheme { DataSaverScreen(onBackClicked = {}, onUpdated = {}) } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun SecurityPreview()     { BeatlyTheme { SecurityScreen(onBackClicked = {}, onChangePin = {}, onChangePassword = {}) } }

@Preview(showBackground = true, showSystemUi = true)
@Composable fun LanguagePreview()     { BeatlyTheme { LanguageScreen(onBackClicked = {}, onChanged = {}) } }