package com.taher.beatly.ui.auth

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.taher.beatly.ui.theme.*

// ── Screen ─────────────────────────────────────────────────────────────────

@Composable
fun ProfileSuccessScreen(
    onContinue: () -> Unit,
    onCallSupport: () -> Unit,
) {
    // Entrance scale animation for the checkmark circle
    val scale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F7)),          // light grey page bg
        contentAlignment = Alignment.Center
    ) {
        // ── Centre content ─────────────────────────────────────────────────
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            // Decorative scattered dots
            Box(contentAlignment = Alignment.Center) {
                DecorDot(size = 10.dp, offsetX = (-80).dp, offsetY = (-20).dp, alpha = 0.25f)
                DecorDot(size = 8.dp, offsetX = 70.dp, offsetY = (-50).dp, alpha = 0.20f)
                DecorDot(size = 12.dp, offsetX = (-50).dp, offsetY = 60.dp, alpha = 0.18f)
                DecorDot(size = 7.dp, offsetX = 90.dp, offsetY = 30.dp, alpha = 0.15f)

                // Purple check circle
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(scale.value)
                        .background(Purple500, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        tint = White,
                        modifier = Modifier.size(56.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Profile Setup Success!",
                style = Headline,
                fontSize = 28.sp,
                color = TextBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Now you can listen to music on this app \n anytime and anywhere.",
                style = BodySmallRegular,
                fontSize = 16.sp,
                color = Gray500,
                textAlign = TextAlign.Center,
                lineHeight = BodySmallRegular.lineHeight
            )
        }

        // ── Bottom actions ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = onContinue,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple500,
                    contentColor = White
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(text = "Continue", style = BodyMediumMedium)
            }

            Spacer(modifier = Modifier.height(16.dp))

            val footerText = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = Gray500,
                        fontFamily = Inter,
                        fontSize = BodySmallRegular.fontSize
                    )
                ) {
                    append("Can't access your Account? ")
                }
                withStyle(
                    SpanStyle(
                        color = Purple500,
                        fontFamily = Inter,
                        fontSize = BodySmallRegular.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                ) {
                    append("Call Support")
                }
            }
            Text(
                text = footerText,
                modifier = Modifier.clickable { onCallSupport() },
                textAlign = TextAlign.Center
            )
        }
    }
}

// ── Decorative dot helper ──────────────────────────────────────────────────

@Composable
private fun DecorDot(
    size: Dp,
    offsetX: Dp,
    offsetY: Dp,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .size(size)
            .offset(x = offsetX, y = offsetY)
            .background(Purple300.copy(alpha = alpha), CircleShape)
    )
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileSuccessPreview() {
    BeatlyTheme {
        ProfileSuccessScreen(
            onContinue = {},
            onCallSupport = {}
        )
    }
}