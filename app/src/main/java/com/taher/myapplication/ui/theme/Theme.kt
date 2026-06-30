package com.beatly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple = Color(0xFF7B61FF)
val PurpleLight = Color(0xFFB8ABFF)
val DarkBackground = Color(0xFF1A1A2E)
val DarkSurface = Color(0xFF16213E)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFB0B0C3)
val TextDark = Color(0xFF1A1A2E)

private val BeatlyColorScheme = lightColorScheme(
    primary = Purple,
    onPrimary = TextWhite,
    background = TextWhite,
    surface = TextWhite,
    onBackground = TextDark,
    onSurface = TextDark,
)

@Composable
fun BeatlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BeatlyColorScheme,
        typography = BeatlyTypography,
        content = content
    )
}