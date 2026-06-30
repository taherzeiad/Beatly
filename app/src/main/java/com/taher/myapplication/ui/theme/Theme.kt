package com.beatly.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val BeatlyLightColorScheme = lightColorScheme(
    primary            = Purple500,
    onPrimary          = White,
    primaryContainer   = Purple300,
    onPrimaryContainer = TextBlack,
    secondary          = Gray500,
    onSecondary        = White,
    background         = White,
    onBackground       = TextBlack,
    surface            = White,
    onSurface          = TextBlack,
    surfaceVariant     = SurfaceFill,
    onSurfaceVariant   = Gray600,
    outline            = Gray200,
    outlineVariant     = Gray100,
)

@Composable
fun BeatlyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BeatlyLightColorScheme,
        typography  = BeatlyTypography,
        content     = content
    )
}