package com.taher.beatly.ui.subscription


import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.taher.beatly.ui.components.AnimatedCheckCircle
import com.taher.beatly.ui.components.AuthSuccessScaffold
import com.taher.beatly.ui.theme.BeatlyTheme

// No ViewModel needed — purely informational

@Composable
fun CongratulationsScreen(
    billingPeriod: String = "1 month",   // pass from nav arg
    onBackToHome: () -> Unit
) {
    AuthSuccessScaffold(
        title = "Congratulations!",
        subtitle = "You have successfully subscribed $billingPeriod premium. Enjoy the benefits!",
        buttonLabel = "Back to home",
        onContinue = onBackToHome,
        onSupport = {}
    ) {
        AnimatedCheckCircle()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CongratulationsPreview() {
    BeatlyTheme { CongratulationsScreen(onBackToHome = {}) }
}