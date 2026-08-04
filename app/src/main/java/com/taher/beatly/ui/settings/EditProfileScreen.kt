package com.taher.beatly.ui.settings

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import com.taher.beatly.R
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.components.AuthFieldLabel
import com.taher.beatly.ui.components.AuthTextField
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

// ═════════════════════════════════════════════════════════════════════════════
// Edit Profile
// ═════════════════════════════════════════════════════════════════════════════

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            Toast.makeText(context, R.string.profile_updated_success, Toast.LENGTH_SHORT).show()
            onUpdated()
        }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Date(millis)
                        val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        viewModel.onBirthDateChanged(formatter.format(date))
                    }
                    showDatePicker = false
                }) {
                    Text("OK", color = Purple500)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Gray500)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.edit_profile), onBackClicked)

            Spacer(modifier = Modifier.height(24.dp))

            // Avatar Section
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                com.taher.beatly.ui.components.BeatlyImage(
                    url = uiState.avatarUrl,
                    modifier = Modifier.fillMaxSize(),
                    shape = CircleShape
                )
                Surface(
                    onClick = { /* Launch image picker */ },
                    shape = CircleShape,
                    color = Purple500,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Change Avatar",
                        tint = White,
                        modifier = Modifier
                            .padding(6.dp)
                            .fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name
                Column {
                    AuthFieldLabel(stringResource(R.string.name))
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(uiState.name, viewModel::onNameChanged, stringResource(R.string.name))
                }
                // Username
                Column {
                    AuthFieldLabel(stringResource(R.string.username))
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(uiState.username, viewModel::onUsernameChanged, stringResource(R.string.username))
                }
                // Birth Date
                Column {
                    AuthFieldLabel(stringResource(R.string.birth_date))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = uiState.birthDate,
                        onValueChange = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        readOnly = true,
                        textStyle = BodySmallRegular.copy(color = Gray950),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = SurfaceFill,
                            focusedContainerColor = SurfaceFill,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                            focusedBorderColor = Purple500
                        ),
                        trailingIcon = {
                            IconButton(onClick = { showDatePicker = true }) {
                                Icon(Icons.Default.CalendarMonth, null, tint = Gray400)
                            }
                        },
                        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                            .also { interactionSource ->
                                LaunchedEffect(interactionSource) {
                                    interactionSource.interactions.collect {
                                        if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                            showDatePicker = true
                                        }
                                    }
                                }
                            }
                    )
                }
                // Mail
                Column {
                    AuthFieldLabel(stringResource(R.string.mail))
                    Spacer(Modifier.height(8.dp))
                    AuthTextField(
                        uiState.mail,
                        viewModel::onMailChanged,
                        stringResource(R.string.mail),
                        keyboardType = KeyboardType.Email
                    )
                }
                // Gender dropdown
                Column {
                    AuthFieldLabel(stringResource(R.string.gender))
                    Spacer(Modifier.height(8.dp))
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        val displayGender = when (uiState.gender) {
                            "Male" -> stringResource(R.string.male)
                            "Female" -> stringResource(R.string.female)
                            "Prefer not to say" -> stringResource(R.string.gender_other)
                            else -> uiState.gender
                        }
                        OutlinedTextField(
                            value = displayGender,
                            onValueChange = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .height(52.dp),
                            readOnly = true,
                            textStyle = BodySmallRegular.copy(color = Gray950),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedContainerColor = SurfaceFill,
                                focusedContainerColor = SurfaceFill,
                                unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent,
                                focusedBorderColor = Purple500
                            ),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }) {
                            val options = listOf(
                                "Male" to R.string.male,
                                "Female" to R.string.female,
                                "Prefer not to say" to R.string.gender_other
                            )
                            options.forEach { (key, res) ->
                                DropdownMenuItem(
                                    text = { Text(stringResource(res), style = BodySmallRegular) },
                                    onClick = {
                                        viewModel.onGenderChanged(key); expanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Dark Mode Toggle
                SettingsToggleRow(
                    label = stringResource(R.string.dark_mode),
                    checked = uiState.isDarkMode,
                    onToggle = viewModel::onDarkModeToggled
                )
            }
        }
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            viewModel::onUpdate,
            enabled = !uiState.isLoading,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Notification
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onUpdated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.notification), onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.items.forEach { item ->
                    SettingsToggleRow(
                        label = stringResource(item.labelRes),
                        checked = item.enabled,
                        onToggle = { viewModel.onToggled(item.id, !item.enabled) }
                    )
                }
            }
        }
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            viewModel::onUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Audio & Video
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AudioVideoScreen(
    viewModel: AudioVideoViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onUpdated()
        }
    }

    if (uiState.isQualityDialogVisible) {
        QualitySelectionDialog(
            onDismiss = viewModel::onQualityDialogDismiss,
            onSelected = viewModel::onQualitySelected,
            currentValue = when (uiState.activeSelectionKey) {
                "wifi" -> uiState.wifiStreamingAudio
                "cellular" -> uiState.cellularStreamingAudio
                "download" -> uiState.downloadQuality
                else -> ""
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.audio_video), onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsSectionLabel(stringResource(R.string.audio_quality))
                SettingsValueRow(stringResource(R.string.wifi_streaming), uiState.wifiStreamingAudio, onClick = { viewModel.onQualityRowClicked("wifi") })
                SettingsValueRow(stringResource(R.string.cellular_streaming), uiState.cellularStreamingAudio, onClick = { viewModel.onQualityRowClicked("cellular") })
                SettingsToggleRow(
                    stringResource(R.string.auto_adjust_quality),
                    checked = uiState.autoAdjustQuality,
                    onToggle = viewModel::onAutoAdjustToggled
                )
                SettingsValueRow(stringResource(R.string.download), uiState.downloadQuality, onClick = { viewModel.onQualityRowClicked("download") })

                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel(stringResource(R.string.video_quality))
                SettingsValueRow(stringResource(R.string.wifi_streaming), uiState.wifiStreamingVideo)
                SettingsValueRow(stringResource(R.string.cellular_streaming), uiState.cellularStreamingVideo)
            }
        }
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            viewModel::onUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

@Composable
fun QualitySelectionDialog(
    onDismiss: () -> Unit,
    onSelected: (String) -> Unit,
    currentValue: String
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.audio_quality)) },
        text = {
            Column {
                val options = listOf("Automatic", "Normal", "High", "Extreme")
                options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelected(option) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = option == currentValue, onClick = { onSelected(option) })
                        Spacer(Modifier.width(8.dp))
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}

// ═════════════════════════════════════════════════════════════════════════════
// Playback
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun PlaybackScreen(
    viewModel: PlaybackViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onUpdated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.playback), onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.settings.forEach { s ->
                    SettingsToggleRow(
                        label = stringResource(s.labelRes),
                        subtitle = stringResource(s.subtitleRes),
                        checked = s.enabled,
                        onToggle = { viewModel.onToggled(s.id) })
                }
            }
        }
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            viewModel::onUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Data Saver & Storage
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun DataSaverScreen(
    viewModel: DataSaverViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onUpdated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onUpdated()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.data_saver), onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsSectionLabel(stringResource(R.string.data_saver))
                SettingsToggleRow(
                    stringResource(R.string.audio_quality),
                    subtitle = stringResource(R.string.audio_quality_saver_desc),
                    checked = uiState.audioQualitySaver,
                    onToggle = viewModel::onAudioQualityToggled
                )

                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel(stringResource(R.string.podcasts))
                SettingsToggleRow(
                    stringResource(R.string.download_audio_only),
                    subtitle = stringResource(R.string.download_audio_only_desc),
                    checked = uiState.downloadAudioOnly,
                    onToggle = viewModel::onDownloadAudioToggled
                )
                SettingsToggleRow(
                    stringResource(R.string.stream_audio_only),
                    subtitle = stringResource(R.string.stream_audio_only_desc),
                    checked = uiState.streamAudioOnly, onToggle = viewModel::onStreamAudioToggled
                )

                Spacer(Modifier.height(4.dp))
                SettingsSectionLabel(stringResource(R.string.storage))
                SettingsValueRow(stringResource(R.string.other_apps), uiState.otherAppsStorage)
                SettingsValueRow(stringResource(R.string.cache), uiState.cacheStorage)
            }
        }
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            viewModel::onUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Security
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun SecurityScreen(
    viewModel: SecurityViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onChangePin: () -> Unit,
    onChangePassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onBackClicked()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.security), onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SettingsToggleRow(
                    stringResource(R.string.remember_me),
                    checked = uiState.rememberMe,
                    onToggle = viewModel::onRememberMeToggled
                )
                SettingsToggleRow(
                    stringResource(R.string.face_id),
                    checked = uiState.faceId,
                    onToggle = viewModel::onFaceIdToggled
                )
                SettingsToggleRow(
                    stringResource(R.string.biometric_id),
                    checked = uiState.biometricId,
                    onToggle = viewModel::onBiometricToggled
                )
                SettingsValueRow(stringResource(R.string.google_authenticator), onClick = {})
                Spacer(Modifier.height(8.dp))
                AuthPrimaryButton(stringResource(R.string.change_pin), onChangePin, enabled = false)
                AuthPrimaryButton(stringResource(R.string.change_password), onChangePassword, enabled = false)
            }
        }
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            viewModel::onUpdate,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// Language
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onChanged: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onChanged()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SettingsTopBar(stringResource(R.string.language), onBackClicked)
            Spacer(Modifier.height(20.dp))
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.groups.forEach { group ->
                    SettingsSectionLabel(group.groupTitle)
                    group.languages.forEach { lang ->
                        val isSelected = lang == uiState.selectedLang
                        Card(
                            onClick = { viewModel.onLanguageSelected(lang) },
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceFill),
                            elevation = CardDefaults.cardElevation(0.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    lang,
                                    style = BodySmallRegular,
                                    color = TextBlack,
                                    modifier = Modifier.weight(1f)
                                )
                                Box(
                                    modifier = Modifier.size(20.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    RadioButton(
                                        selected = isSelected,
                                        onClick = { viewModel.onLanguageSelected(lang) },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Purple500,
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
        AuthPrimaryButton(
            stringResource(R.string.save_changes),
            onChanged,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

// ── Previews ───────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfilePreview() {
    BeatlyTheme { EditProfileScreen(onBackClicked = {}, onUpdated = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NotificationPreview() {
    BeatlyTheme { NotificationScreen(onBackClicked = {}, onUpdated = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AudioVideoPreview() {
    BeatlyTheme { AudioVideoScreen(onBackClicked = {}, onUpdated = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlaybackPreview() {
    BeatlyTheme { PlaybackScreen(onBackClicked = {}, onUpdated = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DataSaverPreview() {
    BeatlyTheme { DataSaverScreen(onBackClicked = {}, onUpdated = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SecurityPreview() {
    BeatlyTheme { SecurityScreen(onBackClicked = {}, onChangePin = {}, onChangePassword = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LanguagePreview() {
    BeatlyTheme { LanguageScreen(onBackClicked = {}, onChanged = {}) }
}

// ═════════════════════════════════════════════════════════════════════════════
// About & Privacy
// ═════════════════════════════════════════════════════════════════════════════

@Composable
fun AboutScreen(onBackClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        SettingsTopBar(stringResource(R.string.about), onBackClicked)
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(40.dp))
            com.taher.beatly.ui.components.BeatlyLogoIcon(size = 80.dp)
            Spacer(Modifier.height(16.dp))
            Text("Beatly Music", style = MaterialTheme.typography.headlineSmall, color = TextBlack)
            Text(stringResource(R.string.version), style = BodySmallRegular, color = Gray500)
            Spacer(Modifier.height(32.dp))
            Text(
                stringResource(R.string.about_beatly_desc),
                style = BodySmallRegular,
                color = Gray600,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun PrivacyPolicyScreen(onBackClicked: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        SettingsTopBar(stringResource(R.string.privacy_policy), onBackClicked)
        Column(
            modifier = Modifier
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                stringResource(R.string.privacy_desc),
                style = BodySmallRegular,
                color = Gray600
            )
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.data_collection_title), style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                stringResource(R.string.data_collection_desc),
                style = BodySmallRegular,
                color = Gray600
            )
            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.usage_title), style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold))
            Text(
                stringResource(R.string.usage_desc),
                style = BodySmallRegular,
                color = Gray600
            )
        }
    }
}
