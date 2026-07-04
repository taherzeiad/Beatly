package com.taher.beatly.ui.auth.resetpassword

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
import com.taher.beatly.ui.components.PasswordRuleItem
import com.taher.beatly.ui.theme.BeatlyTheme
import com.taher.beatly.ui.theme.White

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun ResetPasswordScreen(
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onContinue: () -> Unit,          // → ResetSuccessScreen
    onCallSupport: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ResetPasswordContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onPasswordChanged = viewModel::onPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onPasswordToggled = viewModel::onPasswordVisibilityToggled,
        onConfirmPasswordToggled = viewModel::onConfirmPasswordVisibilityToggled,
        onContinue = { viewModel.onContinueClicked(); onContinue() },
        onCallSupport = onCallSupport
    )
}

// ── Stateless content ──────────────────────────────────────────────────────

@Composable
private fun ResetPasswordContent(
    uiState: ResetPasswordUiState,
    onBackClicked: () -> Unit,
    onPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onPasswordToggled: () -> Unit,
    onConfirmPasswordToggled: () -> Unit,
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
                .padding(top = 60.dp, bottom = 180.dp)
        ) {
            AuthBackButton(onClick = onBackClicked)

            Spacer(modifier = Modifier.height(24.dp))

            AuthTitleBlock(
                title = "Reset Password", subtitle = "Create new password to recover your account"
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ── Password ───────────────────────────────────────────────────
            AuthFieldLabel("Password")
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = uiState.password,
                onValueChange = onPasswordChanged,
                placeholder = "**********",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = uiState.isPasswordVisible,
                onPasswordToggle = onPasswordToggled
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Confirm Password ───────────────────────────────────────────
            AuthFieldLabel("Confirm Password")
            Spacer(modifier = Modifier.height(8.dp))
            AuthTextField(
                value = uiState.confirmPassword,
                onValueChange = onConfirmPasswordChanged,
                placeholder = "**********",
                keyboardType = KeyboardType.Password,
                isPassword = true,
                isPasswordVisible = uiState.isConfirmPasswordVisible,
                onPasswordToggle = onConfirmPasswordToggled,
                isError = uiState.confirmPassword.isNotEmpty() && !uiState.passwordsMatch
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Password rules ─────────────────────────────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.passwordRules.forEach { rule ->
                    PasswordRuleItem(label = rule.label, isMet = rule.isMet)
                }
            }
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
                text = "Continue", enabled = uiState.isFormValid, onClick = onContinue
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthSupportFooter(onClick = onCallSupport)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordEmptyPreview() {
    BeatlyTheme { ResetPasswordScreen(onBackClicked = {}, onContinue = {}, onCallSupport = {}) }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ResetPasswordFilledPreview() {
    BeatlyTheme {
        val vm = ResetPasswordViewModel().apply {
            onPasswordChanged("Str0ng#Pass!")
            onConfirmPasswordChanged("Str0ng#Pass!")
        }
        ResetPasswordScreen(vm, onBackClicked = {}, onContinue = {}, onCallSupport = {})
    }
}