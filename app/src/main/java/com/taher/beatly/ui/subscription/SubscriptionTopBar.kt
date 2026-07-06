package com.taher.beatly.ui.subscription


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.taher.beatly.ui.components.RoundIconButton
import com.taher.beatly.ui.theme.BodyMediumMedium
import com.taher.beatly.ui.theme.TextBlack

// Shared top bar reused across all subscription screens

@Composable
fun SubscriptionTopBar(
    title: String,
    onBackClicked: () -> Unit,
    onMoreClicked: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        RoundIconButton(
            icon = Icons.Default.ArrowBackIosNew,
            onClick = onBackClicked,
            contentDescription = "Back"
        )
        Text(
            text = title,
            style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold),
            color = TextBlack
        )
        if (onMoreClicked != null) {
            RoundIconButton(
                icon = Icons.Default.MoreHoriz,
                onClick = onMoreClicked,
                contentDescription = "More"
            )
        } else {
            Spacer(modifier = Modifier.size(42.dp))
        }
    }
}