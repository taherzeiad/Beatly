package com.beatly.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taher.myapplication.R
import com.beatly.ui.theme.Purple
import com.beatly.ui.theme.PurpleLight
import com.beatly.ui.theme.TextWhite

// ── Logo icon ──────────────────────────────────────────────────────────────

@Composable
fun BeatlyLogoIcon(
    size: Dp = 48.dp,
    tint: Color = Purple
) {
    Icon(
        painter = painterResource(id = R.drawable.music),
        contentDescription = "Beatly Logo",
        tint = tint,
        modifier = Modifier.size(size)
    )
}

// ── Dot Indicators ─────────────────────────────────────────────────────────

@Composable
fun PageIndicators(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = index == currentPage
            Box(
                modifier = Modifier
                    .size(if (isSelected) 10.dp else 8.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Purple
                        else Color.White.copy(alpha = 0.4f)
                    )
            )
        }
    }
}

// ── Primary Button ─────────────────────────────────────────────────────────

@Composable
fun BeatlyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.buttonColors(
            containerColor = PurpleLight,
            contentColor = Color(0xFF3D2B8E)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

// ── Register footer ────────────────────────────────────────────────────────

@Composable
fun RegisterFooter(
    onRegisterClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val annotated = buildAnnotatedString {
        withStyle(SpanStyle(color = TextWhite, fontSize = 14.sp)) {
            append("Haven't registered yet? ")
        }
        withStyle(SpanStyle(color = Purple, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)) {
            append("Register Now")
        }
    }

    TextButton(onClick = onRegisterClicked, modifier = modifier) {
        Text(
            text = annotated,
            textAlign = TextAlign.Center
        )
    }
}