package com.taher.beatly.ui.auth.signIn

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
fun SignInScreen(
    viewModel: SignInViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onSignInSuccess: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegisterClicked: () -> Unit,
    onAppleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SignInContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onEmailChanged = viewModel::onEmailChanged,
        onPasswordChanged = viewModel::onPasswordChanged,
        onRememberToggled = viewModel::onRememberMeToggled,
        onPasswordToggled = viewModel::onPasswordVisibilityToggled,
        onContinue = { viewModel.onSignInClicked(); onSignInSuccess() },
        onForgotPassword = onForgotPassword,
        onRegisterClicked = onRegisterClicked,
        onAppleSignIn = onAppleSignIn,
        onFacebookSignIn = onFacebookSignIn,
    )
}

// ── Stateless content ──────────────────────────────────────────────────────

@Composable
private fun SignInContent(
    uiState: SignInUiState,
    onBackClicked: () -> Unit,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onRememberToggled: () -> Unit,
    onPasswordToggled: () -> Unit,
    onContinue: () -> Unit,
    onForgotPassword: () -> Unit,
    onRegisterClicked: () -> Unit,
    onAppleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 40.dp)
    ) {
        // ── Back button ────────────────────────────────────────────────────
        BackButton(onClick = onBackClicked)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Title ──────────────────────────────────────────────────────────
        Text(
            text = "Sign In",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Sign In to your account",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // ── Email field ────────────────────────────────────────────────────
        BeatlyLabel(text = "Email")
        Spacer(modifier = Modifier.height(8.dp))
        BeatlyTextField(
            value = uiState.email,
            onValueChange = onEmailChanged,
            placeholder = "Enter your email address",
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(20.dp))

        // ── Password field ─────────────────────────────────────────────────
        BeatlyLabel(text = "Password")
        Spacer(modifier = Modifier.height(8.dp))
        BeatlyTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            placeholder = "Enter your password",
            keyboardType = KeyboardType.Password,
            isPassword = true,
            isPasswordVisible = uiState.isPasswordVisible,
            onPasswordToggle = onPasswordToggled
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ── Remember me + Forgot password ─────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RememberMeCheckbox(
                checked = uiState.rememberMe,
                onToggle = onRememberToggled
            )
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── Continue button ────────────────────────────────────────────────
        Button(
            onClick = onContinue,
            enabled = uiState.isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = "Continue", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ── OR divider ─────────────────────────────────────────────────────
        OrDivider()

        Spacer(modifier = Modifier.height(24.dp))

        // ── Social buttons ─────────────────────────────────────────────────
        SocialButton(
            iconRes = R.drawable.apple,
            label = "Sign In with Apple",
            onClick = onAppleSignIn,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialButton(
            iconRes = R.drawable.facebook,
            label = "Sign In with Facebook",
            onClick = onFacebookSignIn,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Bottom link (changes based on state) ───────────────────────────
        val footerText = buildAnnotatedString {
            val isNew = uiState.email.isBlank() && uiState.password.isBlank()
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontFamily = Inter
                )
            ) {
                append(if (isNew) "Have an account? " else "Don't have an account? ")
            }
            withStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontFamily = Inter,
                    fontWeight = FontWeight.SemiBold
                )
            ) {
                append(if (isNew) "Login" else "Register")
            }
        }
        Text(
            text = footerText,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onRegisterClicked() },
            textAlign = TextAlign.Center
        )
    }
}

// ── Sub-components ─────────────────────────────────────────────────────────

@Composable
private fun BackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.outlineVariant)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun BeatlyLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun BeatlyTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = {
            Text(text = placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !isPasswordVisible)
            PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordToggle?.invoke() }) {
                    Icon(
                        painter = painterResource(
                            if (isPasswordVisible) R.drawable.eyeclosed  // عين مفتوحة
                            else R.drawable.eyeclosed  // عين مغلقة
                        ),
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = if (isPasswordVisible) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else null
    )
}

@Composable
private fun RememberMeCheckbox(
    checked: Boolean,
    onToggle: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onToggle() }
    ) {
        Icon(
            imageVector = if (checked) Icons.Default.CheckBox
            else Icons.Default.CheckBoxOutlineBlank,
            contentDescription = "Remember me",
            tint = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Remember me", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
private fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
        Text(
            text = "  OR  ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun SocialButton(
    iconRes: Int,
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(50),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(22.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenEmptyPreview() {
    BeatlyTheme {
        SignInScreen(
            onBackClicked = {},
            onSignInSuccess = {},
            onForgotPassword = {},
            onRegisterClicked = {},
            onAppleSignIn = {},
            onFacebookSignIn = {}
        )
    }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignInScreenFilledPreview() {
    BeatlyTheme {
        val vm = SignInViewModel(
            authRepository = TODO()
        ).apply {
            onEmailChanged("mardia@gmail.com")
            onPasswordChanged("***********")
            onRememberMeToggled()
        }
        SignInScreen(
            viewModel = vm,
            onBackClicked = {},
            onSignInSuccess = {},
            onForgotPassword = {},
            onRegisterClicked = {},
            onAppleSignIn = {},
            onFacebookSignIn = {}
        )
    }
}
