package com.taher.beatly.ui.auth.signup

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
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taher.beatly.ui.theme.BeatlyTheme
import com.taher.beatly.ui.theme.BodyMediumMedium
import com.taher.beatly.ui.theme.BodySmallRegular
import com.taher.beatly.ui.theme.BodyXSmallRegular
import com.taher.beatly.ui.theme.Gray100
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray950
import com.taher.beatly.ui.theme.Headline
import com.taher.beatly.ui.theme.Inter
import com.taher.beatly.ui.theme.Purple300
import com.taher.beatly.ui.theme.Purple500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White
import com.taher.beatly.R

// ── Screen entry point ─────────────────────────────────────────────────────

@Composable
fun SignUpScreen(
    viewModel          : SignUpViewModel = viewModel(),
    onBackClicked      : () -> Unit,
    onSignUpSuccess    : () -> Unit,
    onTermsClicked     : () -> Unit,
    onPrivacyClicked   : () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        onContinue                   = { viewModel.onSignUpClicked(); onSignUpSuccess() },
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
            .background(White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
            .padding(top = 60.dp, bottom = 40.dp)
    ) {
        // ── Back ───────────────────────────────────────────────────────────
        SignUpBackButton(onClick = onBackClicked)

        Spacer(modifier = Modifier.height(24.dp))

        // ── Title ──────────────────────────────────────────────────────────
        Text(text = "Sign Up", style = Headline, color = TextBlack)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "Set up your profile before continue", style = BodySmallRegular, color = Gray500)

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
                style = BodyXSmallRegular,
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

        // ── Continue button ────────────────────────────────────────────────
        Button(
            onClick  = onContinue,
            enabled  = uiState.isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape    = RoundedCornerShape(50),
            colors   = ButtonDefaults.buttonColors(
                containerColor         = Purple500,
                contentColor           = White,
                disabledContainerColor = Purple300,
                disabledContentColor   = TextBlack
            ),
            elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
            Text(text = "Continue", style = BodyMediumMedium)
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
            .background(Gray100)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back",
            tint               = TextBlack,
            modifier           = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun SignUpLabel(text: String) {
    Text(text = text, style = BodyMediumMedium, color = TextBlack)
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
        placeholder   = { Text(placeholder, style = BodySmallRegular, color = Gray400) },
        textStyle     = BodySmallRegular.copy(color = Gray950),
        singleLine    = true,
        isError       = isError,
        shape         = RoundedCornerShape(12.dp),
        colors        = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SurfaceFill,
            focusedContainerColor   = SurfaceFill,
            unfocusedBorderColor    = Color.Transparent,
            focusedBorderColor      = Purple500,
            errorBorderColor        = MaterialTheme.colorScheme.error,
            errorContainerColor     = SurfaceFill,
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
                        tint               = Gray400
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
            tint               = if (checked) Purple500 else Gray400,
            modifier           = Modifier
                .size(20.dp)
                .clickable { onToggle() }
        )
        Spacer(modifier = Modifier.width(10.dp))

        val text = buildAnnotatedString {
            withStyle(SpanStyle(color = TextBlack, fontFamily = Inter, fontSize = BodySmallRegular.fontSize)) {
                append("By creating an account, you agree to our ")
            }
            withStyle(SpanStyle(color = Purple500, fontFamily = Inter, fontSize = BodySmallRegular.fontSize, fontWeight = FontWeight.SemiBold)) {
                append("Terms and Conditions")
            }
            withStyle(SpanStyle(color = TextBlack, fontFamily = Inter, fontSize = BodySmallRegular.fontSize)) {
                append(" and ")
            }
            withStyle(SpanStyle(color = Purple500, fontFamily = Inter, fontSize = BodySmallRegular.fontSize, fontWeight = FontWeight.SemiBold)) {
                append("Privacy Notice.")
            }
        }
        Text(text = text, lineHeight = BodySmallRegular.lineHeight)
    }
}

// ── Preview ────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpEmptyPreview() {
    BeatlyTheme { SignUpScreen(onBackClicked = {}, onSignUpSuccess = {}, onTermsClicked = {}, onPrivacyClicked = {}) }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpFilledPreview() {
    BeatlyTheme {
        val vm = SignUpViewModel().apply {
            onEmailChanged("Wilson9@gmail.com")
            onUsernameChanged("Jenny Wilson")
            onPasswordChanged("***********")
            onConfirmPasswordChanged("***********")
            onTermsToggled()
        }
        SignUpScreen(vm, onBackClicked = {}, onSignUpSuccess = {}, onTermsClicked = {}, onPrivacyClicked = {})
    }
}