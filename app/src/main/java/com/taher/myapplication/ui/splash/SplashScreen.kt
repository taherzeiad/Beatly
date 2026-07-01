package com.beatly.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.beatly.ui.components.BeatlyLogoIcon
import com.beatly.ui.theme.BeatlyTheme
import com.beatly.ui.theme.BodySmallRegular
import com.beatly.ui.theme.Gray500
import com.beatly.ui.theme.Headline
import com.beatly.ui.theme.Purple500
import com.beatly.ui.theme.TextBlack
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    val alpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        alpha.animateTo(targetValue = 1f, animationSpec = tween(800))
        delay(1_500L.milliseconds)
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

            // ── Logo row ───────────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                BeatlyLogoIcon(size = 50.dp, tint = Purple500)

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Beatly",
                    style = Headline,
                    color = TextBlack,
                    fontSize = 40.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ── Tagline ────────────────────────────────────────────────────
            Text(
                text = "Feel the rhythm, live the beat.",
                style = BodySmallRegular,
                color = Gray500,
                textAlign = TextAlign.Center,
                fontSize = 13.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    BeatlyTheme { SplashScreen(onSplashFinished = {}) }
}