package com.taher.beatly.ui.auth.forgotpassword

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.components.AuthBackButton
import com.taher.beatly.ui.components.AuthFieldLabel
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.components.AuthSupportFooter
import com.taher.beatly.ui.components.AuthTextField
import com.taher.beatly.ui.components.AuthTitleBlock
import com.taher.beatly.ui.theme.BeatlyTheme

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun ForgotPasswordScreen(
    viewModel     : ForgotPasswordViewModel = hiltViewModel(),
    onBackClicked : () -> Unit,
    onContinue    : () -> Unit,          // → RecoveryEmailSentScreen
    onCallSupport : () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ForgotPasswordContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onEmailChanged = viewModel::onEmailChanged,
        onContinue = { viewModel.onContinueClicked(onSuccess = onContinue) },
        onCallSupport = onCallSupport
    )
}

// ── Stateless content ──────────────────────────────────────────────────────

@Composable
private fun ForgotPasswordContent(
    uiState: ForgotPasswordUiState,
    onBackClicked: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onContinue: () -> Unit,
    onCallSupport: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Scrollable form ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(top = 60.dp, bottom = 160.dp)
        ) {
            AuthBackButton(onClick = onBackClicked)

            Spacer(modifier = Modifier.height(24.dp))

            AuthTitleBlock(
                title = "Forgot Password",
                subtitle = "enter your email address and we will help you to restore your account"
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthFieldLabel("Email")
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = uiState.email,
                onValueChange = onEmailChanged,
                placeholder = "Enter your email address",
                keyboardType = KeyboardType.Email
            )
        }

        // ── Pinned bottom actions ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthPrimaryButton(
                text = "Continue",
                enabled = uiState.isFormValid,
                onClick = onContinue
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthSupportFooter(onClick = onCallSupport)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordEmptyPreview() {
    BeatlyTheme { ForgotPasswordScreen(onBackClicked = {}, onContinue = {}, onCallSupport = {}) }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ForgotPasswordFilledPreview() {
    BeatlyTheme {
        // In a real app, you shouldn't instantiate ViewModel like this in Preview
        // but for a quick fix, we'll just show the empty one or a mock.
        ForgotPasswordScreen(onBackClicked = {}, onContinue = {}, onCallSupport = {})
    }
}
