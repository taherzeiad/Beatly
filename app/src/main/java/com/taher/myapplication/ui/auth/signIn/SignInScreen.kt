package com.beatly.ui.auth

import android.annotation.SuppressLint
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.taher.myapplication.R
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.beatly.ui.theme.*

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun SignInScreen(
    viewModel: SignInViewModel = viewModel(),
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
            .background(White)
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
            style = Headline,
            color = TextBlack
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Sign In to your account",
            style = BodySmallRegular,
            color = Gray500
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
                style = BodySmallRegular,
                color = TextBlack,
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
                containerColor = Purple500,
                contentColor = White,
                disabledContainerColor = Purple300,
                disabledContentColor = TextBlack
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = "Continue", style = BodyMediumMedium)
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
            containerColor = White,
            contentColor = TextBlack
        )

        Spacer(modifier = Modifier.height(12.dp))

        SocialButton(
            iconRes = R.drawable.facebook,
            label = "Sign In with Facebook",
            onClick = onFacebookSignIn,
            containerColor = White,
            contentColor = TextBlack
        )

        Spacer(modifier = Modifier.height(24.dp))

        // ── Bottom link (changes based on state) ───────────────────────────
        val footerText = buildAnnotatedString {
            val isNew = uiState.email.isBlank() && uiState.password.isBlank()
            withStyle(
                SpanStyle(
                    color = Gray500,
                    fontSize = BodySmallRegular.fontSize,
                    fontFamily = Inter
                )
            ) {
                append(if (isNew) "Have an account? " else "Don't have an account? ")
            }
            withStyle(
                SpanStyle(
                    color = Purple500,
                    fontSize = BodySmallRegular.fontSize,
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
            .background(Gray100)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back",
            tint = TextBlack,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun BeatlyLabel(text: String) {
    Text(
        text = text,
        style = BodyMediumMedium,
        color = TextBlack
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
            Text(text = placeholder, style = BodySmallRegular, color = Gray400)
        },
        textStyle = BodySmallRegular.copy(color = Gray950),
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SurfaceFill,
            focusedContainerColor = SurfaceFill,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Purple500,
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
                        tint = if (isPasswordVisible) Purple500 else Gray400  // يتغير اللون
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
            tint = if (checked) Purple500 else Gray400,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "Remember me", style = BodySmallRegular, color = TextBlack)
    }
}

@Composable
private fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Gray200)
        Text(
            text = "  OR  ",
            style = BodySmallRegular,
            color = Gray500
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Gray200)
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
        border = androidx.compose.foundation.BorderStroke(1.dp, Gray200)
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = label,
            modifier = Modifier.size(22.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, style = BodyMediumMedium, color = contentColor)
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
        val vm = SignInViewModel().apply {
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