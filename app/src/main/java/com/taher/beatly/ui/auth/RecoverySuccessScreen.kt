package com.taher.beatly.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.taher.beatly.ui.components.AnimatedCheckCircle
import com.taher.beatly.ui.components.AuthSuccessScaffold
import com.taher.beatly.ui.theme.BeatlyTheme

// No ViewModel needed — purely informational screen

@Composable
fun RecoverySuccessScreen(
    onContinue    : () -> Unit,   // → Home or SignIn
    onCallSupport : () -> Unit,
) {
    AuthSuccessScaffold(
        title       = "Recovery Success!",
        subtitle    = "Now you can listen to music on this app anytime and anywhere.",
        buttonLabel = "Continue",
        onContinue  = onContinue,
        onSupport   = onCallSupport,
    ) {
        AnimatedCheckCircle()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecoverySuccessPreview() {
    BeatlyTheme { RecoverySuccessScreen(onContinue = {}, onCallSupport = {}) }
}