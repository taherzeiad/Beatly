package com.taher.beatly.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
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
import androidx.compose.ui.unit.dp
import com.taher.beatly.ui.theme.BodyMediumMedium
import com.taher.beatly.ui.theme.BodySmallRegular
import com.taher.beatly.ui.theme.Gray100
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray950
import com.taher.beatly.ui.theme.Green500
import com.taher.beatly.ui.theme.Headline
import com.taher.beatly.ui.theme.Inter
import com.taher.beatly.ui.theme.Purple300
import com.taher.beatly.ui.theme.Purple500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White
import com.taher.beatly.R


// ── Back button ────────────────────────────────────────────────────────────

@Composable
fun AuthBackButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Gray100)
            .clickable { onClick() }, contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBackIosNew,
            contentDescription = "Back",
            tint = TextBlack,
            modifier = Modifier.size(16.dp)
        )
    }
}

// ── Screen title block ─────────────────────────────────────────────────────

@Composable
fun AuthTitleBlock(title: String, subtitle: String) {
    Text(text = title, style = Headline, color = TextBlack)
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = subtitle, style = BodySmallRegular, color = Gray500)
}

// ── Field label ────────────────────────────────────────────────────────────

@Composable
fun AuthFieldLabel(text: String) {
    Text(text = text, style = BodyMediumMedium, color = TextBlack)
}

// ── Text field ─────────────────────────────────────────────────────────────

@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggle: (() -> Unit)? = null,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        placeholder = { Text(placeholder, style = BodySmallRegular, color = Gray400) },
        textStyle = BodySmallRegular.copy(color = Gray950),
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = SurfaceFill,
            focusedContainerColor = SurfaceFill,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Purple500,
            errorContainerColor = SurfaceFill,
            errorBorderColor = MaterialTheme.colorScheme.error
        ),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        trailingIcon = if (isPassword) {
            {
                IconButton(onClick = { onPasswordToggle?.invoke() }) {
                    Icon(
                        painter = painterResource(
                            if (isPasswordVisible) R.drawable.eyeclosed
                            else R.drawable.eyeclosed
                        ), contentDescription = null, tint = Gray400
                    )
                }
            }
        } else null)
}

// ── Primary action button ──────────────────────────────────────────────────

@Composable
fun AuthPrimaryButton(
    text: String, onClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
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
        Text(text = text, style = BodyMediumMedium)
    }
}

// ── "Can't access? Call Support" footer ───────────────────────────────────

@Composable
fun AuthSupportFooter(onClick: () -> Unit) {
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = Gray500, fontFamily = Inter, fontSize = BodySmallRegular.fontSize
            )
        ) {
            append("Can't access your Account? ")
        }
        withStyle(
            SpanStyle(
                color = Purple500,
                fontFamily = Inter,
                fontSize = BodySmallRegular.fontSize,
                fontWeight = FontWeight.SemiBold
            )
        ) {
            append("Call Support")
        }
    }
    Text(
        text = text,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        textAlign = TextAlign.Center
    )
}

// ── Password rule row ──────────────────────────────────────────────────────

@Composable
fun PasswordRuleItem(label: String, isMet: Boolean) {
    Row(
        verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = if (isMet) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
            contentDescription = null,
            tint = if (isMet) Green500 else Gray400,
            modifier = Modifier
                .size(20.dp)
                .padding(top = 1.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label, style = BodySmallRegular, color = if (isMet) TextBlack else Gray500
        )
    }
}

// ── Success/info screen scaffold ───────────────────────────────────────────
// Reused by RecoveryEmail, RecoverySuccess, ProfileSuccess

@Composable
fun AuthSuccessScaffold(
    title: String,
    subtitle: String,
    buttonLabel: String,
    onContinue: () -> Unit,
    onSupport: () -> Unit,
    topContent: @Composable () -> Unit   // caller places the icon/illustration
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White), contentAlignment = Alignment.Center
    ) {
        // Centre block
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            topContent()

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = title, style = Headline, color = TextBlack, textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = subtitle,
                style = BodySmallRegular,
                color = Gray500,
                textAlign = TextAlign.Center
            )
        }

        // Bottom actions
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 44.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AuthPrimaryButton(text = buttonLabel, onClick = onContinue)
            Spacer(modifier = Modifier.height(16.dp))
            AuthSupportFooter(onClick = onSupport)
        }
    }
}

// ── Animated check circle (shared by success screens) ─────────────────────

@Composable
fun AnimatedCheckCircle() {
    // Import spring animation at call site or keep it self-contained here
    val scale = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f, animationSpec = androidx.compose.animation.core.spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(contentAlignment = Alignment.Center) {
        // Decorative dots
        AuthDecorDot(10.dp, (-80).dp, (-20).dp, 0.25f)
        AuthDecorDot(8.dp, 70.dp, (-50).dp, 0.20f)
        AuthDecorDot(12.dp, (-50).dp, 60.dp, 0.18f)
        AuthDecorDot(7.dp, 90.dp, 30.dp, 0.15f)

        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value)
                .background(Purple500, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(56.dp)
            )
        }
    }
}

@Composable
fun AuthDecorDot(
    size: androidx.compose.ui.unit.Dp,
    offsetX: androidx.compose.ui.unit.Dp,
    offsetY: androidx.compose.ui.unit.Dp,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .size(size)
            .offset(x = offsetX, y = offsetY)
            .background(Purple300.copy(alpha = alpha), CircleShape)
    )
}