@file:Suppress("DEPRECATION")

package com.taher.beatly.ui.auth.signup

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.theme.*
import com.taher.beatly.R

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun SignUpScreen(
    viewModel          : SignUpViewModel = hiltViewModel(),
    onBackClicked      : () -> Unit,
    onSignUpSuccess    : () -> Unit,
    onTermsClicked     : () -> Unit,
    onPrivacyClicked   : () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignUpSuccess()
        }
    }

    SignUpContent(
        uiState                      = uiState,
        onBackClicked                = onBackClicked,
        onEmailChanged               = viewModel::onEmailChanged,
        onUsernameChanged            = viewModel::onUsernameChanged,
        onPasswordChanged            = viewModel::onPasswordChanged,
        onConfirmPasswordChanged     = viewModel::onConfirmPasswordChanged,
        onPasswordToggled            = viewModel::onPasswordVisibilityToggled,
        onConfirmPasswordToggled     = viewModel::onConfirmPasswordVisibilityToggled,
        onTermsToggled               = viewModel::onTermsToggled,
        onContinue                   = viewModel::onSignUpClicked,
        onTermsClicked               = onTermsClicked,
        onPrivacyClicked             = onPrivacyClicked,
    )
}

// ── Stateless content ──────────────────────────────────────────────────────

@Composable
private fun SignUpContent(
    uiState                  : SignUpUiState,
    onBackClicked            : () -> Unit,
    onEmailChanged           : (String) -> Unit,
    onUsernameChanged        : (String) -> Unit,
    onPasswordChanged        : (String) -> Unit,
    onConfirmPasswordChanged : (String) -> Unit,
    onPasswordToggled        : () -> Unit,
    onConfirmPasswordToggled : () -> Unit,
    onTermsToggled           : () -> Unit,
    onContinue               : () -> Unit,
    onTermsClicked           : () -> Unit,
    onPrivacyClicked         : () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 40.dp)
    ) {
        // ── Back ───────────────────────────────────────────────────────────
        SignUpBackButton(onClick = onBackClicked)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Title ──────────────────────────────────────────────────────────
        Text(text = "Sign Up", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Set up your profile before continue", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(32.dp))

        // ── Email ──────────────────────────────────────────────────────────
        SignUpLabel("Email")
        Spacer(modifier = Modifier.height(8.dp))
        SignUpTextField(
            value         = uiState.email,
            onValueChange = onEmailChanged,
            placeholder   = "Enter your email address",
            keyboardType  = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Username ───────────────────────────────────────────────────────
        SignUpLabel("Create Username")
        Spacer(modifier = Modifier.height(8.dp))
        SignUpTextField(
            value         = uiState.username,
            onValueChange = onUsernameChanged,
            placeholder   = "Create Username"
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Password ───────────────────────────────────────────────────────
        SignUpLabel("Create Password")
        Spacer(modifier = Modifier.height(8.dp))
        SignUpTextField(
            value             = uiState.password,
            onValueChange     = onPasswordChanged,
            placeholder       = "Create Password",
            keyboardType      = KeyboardType.Password,
            isPassword        = true,
            isPasswordVisible = uiState.isPasswordVisible,
            onPasswordToggle  = onPasswordToggled
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Confirm Password ───────────────────────────────────────────────
        SignUpLabel("Confirm Password")
        Spacer(modifier = Modifier.height(8.dp))
        SignUpTextField(
            value             = uiState.confirmPassword,
            onValueChange     = onConfirmPasswordChanged,
            placeholder       = "Confirm Password",
            keyboardType      = KeyboardType.Password,
            isPassword        = true,
            isPasswordVisible = uiState.isConfirmPasswordVisible,
            onPasswordToggle  = onConfirmPasswordToggled,
            isError           = uiState.passwordMatchError
        )
        if (uiState.passwordMatchError) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text  = "Passwords do not match",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── Terms checkbox ─────────────────────────────────────────────────
        TermsRow(
            checked          = uiState.isTermsAccepted,
            onToggle         = onTermsToggled,
            onTermsClicked   = onTermsClicked,
            onPrivacyClicked = onPrivacyClicked
        )

        Spacer(modifier = Modifier.height(28.dp))

        // ── Error Message ──────────────────────────────────────────────────
        if (uiState.errorMessage != null) {
            Text(
                text = uiState.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ── Continue button ────────────────────────────────────────────────
        Button(
            onClick  = onContinue,
            enabled  = uiState.isFormValid && !uiState.isLoading,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape    = RoundedCornerShape(50),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = MaterialTheme.colorScheme.primary,
                contentColor           = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor   = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(text = "Continue", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────

@Composable
private fun SignUpBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back",
            tint               = MaterialTheme.colorScheme.onBackground,
            modifier           = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SignUpLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
}

@Composable
private fun SignUpTextField(
    value             : String,
    onValueChange     : (String) -> Unit,
    placeholder       : String,
    keyboardType      : KeyboardType = KeyboardType.Text,
    isPassword        : Boolean = false,
    isPasswordVisible : Boolean = false,
    onPasswordToggle  : (() -> Unit)? = null,
    isError           : Boolean = false
) {
    OutlinedTextField(
        value         = value,
        onValueChange = onValueChange,
        modifier      = Modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder   = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        textStyle     = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine    = true,
        isError       = isError,
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor    = Color.Transparent,
            focusedBorderColor      = MaterialTheme.colorScheme.primary,
            errorBorderColor        = MaterialTheme.colorScheme.error,
            errorContainerColor     = MaterialTheme.colorScheme.surfaceVariant,
        ),
        keyboardOptions      = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !isPasswordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordToggle?.invoke() }) {
                    Icon(
                        painter            = painterResource(
                            if (isPasswordVisible) R.drawable.eyeclosed
                            else R.drawable.eyeclosed // TODO: Use eyeopen when available
                        ),
                        contentDescription = null,
                        tint               = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null
    )
}

@Composable
private fun TermsRow(
    checked          : Boolean,
    onToggle         : () -> Unit,
    onTermsClicked   : () -> Unit,
    onPrivacyClicked : () -> Unit,
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector        = if (checked) Icons.Default.CheckBox
            else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = "Accept terms",
            tint               = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier
                .size(20.dp)
                .clickable { onToggle() }
        )
        Spacer(modifier = Modifier.width(10.dp))

        val text = buildAnnotatedString {
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontFamily = Inter, fontSize = MaterialTheme.typography.bodyMedium.fontSize)) {
                append("By creating an account, you agree to our ")
            }
            pushStringAnnotation(tag = "terms", annotation = "terms")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontFamily = Inter, fontSize = MaterialTheme.typography.bodyMedium.fontSize, fontWeight = FontWeight.SemiBold)) {
                append("Terms and Conditions")
            }
            pop()
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground, fontFamily = Inter, fontSize = MaterialTheme.typography.bodyMedium.fontSize)) {
                append(" and ")
            }
            pushStringAnnotation(tag = "privacy", annotation = "privacy")
            withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary, fontFamily = Inter, fontSize = MaterialTheme.typography.bodyMedium.fontSize, fontWeight = FontWeight.SemiBold)) {
                append("Privacy Notice.")
            }
            pop()
        }
        ClickableText(
            text = text,
            style = LocalTextStyle.current.copy(lineHeight = MaterialTheme.typography.bodyMedium.lineHeight),
            onClick = { offset ->
                text.getStringAnnotations(tag = "terms", start = offset, end = offset).firstOrNull()?.let {
                    onTermsClicked()
                }
                text.getStringAnnotations(tag = "privacy", start = offset, end = offset).firstOrNull()?.let {
                    onPrivacyClicked()
                }
            }
        )
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpEmptyPreview() {
    BeatlyTheme { SignUpScreen(onBackClicked = {}, onSignUpSuccess = {}, onTermsClicked = {}, onPrivacyClicked = {}) }
}

/*
@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpFilledPreview() {
    BeatlyTheme {
        // Requires mock repository
    }
}
*/
