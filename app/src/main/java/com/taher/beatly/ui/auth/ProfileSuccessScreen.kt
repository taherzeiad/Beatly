package com.taher.beatly.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.taher.beatly.ui.components.AnimatedCheckCircle
import com.taher.beatly.ui.components.AuthSuccessScaffold
import com.taher.beatly.ui.theme.BeatlyTheme

@Composable
fun ProfileSuccessScreen(
    onContinue: () -> Unit,
    onCallSupport: () -> Unit,
) {
    AuthSuccessScaffold(
        title = "Profile Setup Success!",
        subtitle = "Now you can listen to music on this app \n anytime and anywhere.",
        buttonLabel = "Continue",
        onContinue = onContinue,
        onSupport = onCallSupport
    ) {
        AnimatedCheckCircle()
    }
}

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
