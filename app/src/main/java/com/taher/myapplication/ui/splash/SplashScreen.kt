package com.beatly.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beatly.ui.components.BeatlyLogoIcon
import com.beatly.ui.theme.BeatlyTheme
import com.beatly.ui.theme.TextDark
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        delay(1500L)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .alpha(alpha.value),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BeatlyLogoIcon(size = 48.dp)

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Beatly",
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Feel the rhythm, live the beat.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF6B6B6B),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    BeatlyTheme {
        SplashScreen(onSplashFinished = {})
    }
}