package com.taher.beatly.ui.auth.recoveryemail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.taher.beatly.ui.components.AnimatedCheckCircle
import com.taher.beatly.ui.components.AuthSuccessScaffold
import com.taher.beatly.ui.theme.BeatlyTheme

// No ViewModel needed — purely informational screen

@Composable
fun RecoveryEmailSentScreen(
    onContinue    : () -> Unit,   // → ResetPasswordScreen
    onCallSupport : () -> Unit,
) {
    AuthSuccessScaffold(
        title       = "We've sent an Recovery Email",
        subtitle    = "Check your email for a link to reset your password. If it doesn't appear within a few minutes, check your spam folder.",
        buttonLabel = "Continue",
        onContinue  = onContinue,
        onSupport   = onCallSupport,
    ) {
        AnimatedCheckCircle()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RecoveryEmailSentPreview() {
    BeatlyTheme { RecoveryEmailSentScreen(onContinue = {}, onCallSupport = {}) }
}