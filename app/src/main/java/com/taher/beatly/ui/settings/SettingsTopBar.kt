package com.taher.beatly.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.subscription.SubscriptionTopBar
import com.taher.beatly.ui.theme.BodyMediumMedium
import com.taher.beatly.ui.theme.BodySmallRegular
import com.taher.beatly.ui.theme.BodyXSmallRegular
import com.taher.beatly.ui.theme.Gray200
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Green500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White

// ── Shared top bar ─────────────────────────────────────────────────────────
// Reused across all settings sub-screens

@Composable
fun SettingsTopBar(title: String, onBackClicked: () -> Unit) {
    SubscriptionTopBar(
        title = title,
        onBackClicked = onBackClicked,
        onMoreClicked = {}
    )
}

// ── Section label ──────────────────────────────────────────────────────────

@Composable
fun SettingsSectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
        color = TextBlack,
        modifier = modifier.padding(bottom = 10.dp)
    )
}

// ── Row with value + arrow ─────────────────────────────────────────────────

@Composable
fun SettingsValueRow(
    label: String,
    value: String = "",
    onClick: () -> Unit = {}
) {
    Card(
        onClick = onClick,
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
            Text(label, style = BodySmallRegular, color = TextBlack, modifier = Modifier.weight(1f))
            if (value.isNotEmpty()) {
                Text(value, style = BodySmallRegular, color = Gray500)
                Spacer(modifier = Modifier.width(4.dp))
            }
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Gray400,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ── Row with toggle ────────────────────────────────────────────────────────

@Composable
fun SettingsToggleRow(
    label: String,
    subtitle: String = "",
    checked: Boolean,
    onToggle: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceFill),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (subtitle.isEmpty()) 4.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(label, style = BodySmallRegular, color = TextBlack)
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(subtitle, style = BodyXSmallRegular, color = Gray500)
                }
            }
            Switch(
                checked = checked,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = White,
                    checkedTrackColor = Green500,
                    uncheckedThumbColor = White,
                    uncheckedTrackColor = Gray200
                )
            )
        }
    }
}

// ── Pinned update button ───────────────────────────────────────────────────

@Composable
fun BoxScope.SettingsUpdateButton(label: String = "Update", onClick: () -> Unit) {
    AuthPrimaryButton(
        text = label,
        onClick = onClick,
        modifier = Modifier
            .align(Alignment.BottomCenter)
            .padding(horizontal = 20.dp, vertical = 40.dp)
    )
}