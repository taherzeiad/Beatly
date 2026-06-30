package com.beatly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.beatly.ui.theme.BodyMediumMedium
import com.beatly.ui.theme.BodySmallRegular
import com.beatly.ui.theme.Gray400
import com.beatly.ui.theme.Purple300
import com.beatly.ui.theme.Purple500
import com.beatly.ui.theme.TextBlack
import com.beatly.ui.theme.White

// ── Logo icon ──────────────────────────────────────────────────────────────

@Composable
fun BeatlyLogoIcon(
    size: Dp = 48.dp,
    tint: Color = Purple500
) {
    Icon(
        imageVector    = Icons.Filled.MusicNote,
        contentDescription = "Beatly Logo",
        tint           = tint,
        modifier       = Modifier.size(size)
    )
}

// ── Page dot indicators ────────────────────────────────────────────────────

@Composable
fun PageIndicators(
    pageCount   : Int,
    currentPage : Int,
    modifier    : Modifier = Modifier
) {
    Row(
        modifier            = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment   = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Purple500
                        else White.copy(alpha = 0.40f)
                    )
            )
        }
    }
}

// ── Primary button ─────────────────────────────────────────────────────────
// Active state  → Purple500 bg / White label
// Disabled state → Purple300 bg / TextBlack label  (matches Figma)

@Composable
fun BeatlyPrimaryButton(
    text     : String,
    onClick  : () -> Unit,
    modifier : Modifier = Modifier,
    enabled  : Boolean  = true
) {
    Button(
        onClick   = onClick,
        enabled   = enabled,
        modifier  = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape     = RoundedCornerShape(50),
        colors    = ButtonDefaults.buttonColors(
            containerColor         = Purple500,
            contentColor           = White,
            disabledContainerColor = Purple300,
            disabledContentColor   = TextBlack
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text  = text,
            style = BodyMediumMedium,
            color = if (enabled) White else TextBlack
        )
    }
}

// ── "Haven't registered yet?" footer ──────────────────────────────────────

@Composable
fun RegisterFooter(
    onRegisterClicked : () -> Unit,
    modifier          : Modifier = Modifier
) {
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(
            color      = White,
            fontSize   = BodySmallRegular.fontSize,
            fontFamily = BodySmallRegular.fontFamily,
            fontWeight = FontWeight.Normal
        )) { append("Haven't registered yet? ") }

        withStyle(SpanStyle(
            color      = Purple300,           // lighter purple on dark bg
            fontSize   = BodySmallRegular.fontSize,
            fontFamily = BodySmallRegular.fontFamily,
            fontWeight = FontWeight.SemiBold
        )) { append("Register Now") }
    }

    TextButton(onClick = onRegisterClicked, modifier = modifier) {
        Text(text = annotated, textAlign = TextAlign.Center)
    }
}

// ── Section divider (reusable) ─────────────────────────────────────────────

@Composable
fun BeatlyDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier  = modifier,
        thickness = 1.dp,
        color     = Gray400.copy(alpha = 0.20f)
    )
}