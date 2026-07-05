package com.taher.beatly.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import com.taher.beatly.R


// ── Back button ────────────────────────────────────────────────────────────

@Composable
fun AuthBackButton(onClick: () -> Unit) {
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

// ── Screen title block ─────────────────────────────────────────────────────

@Composable
fun AuthTitleBlock(title: String, subtitle: String) {
    Text(text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
    Spacer(modifier = Modifier.height(6.dp))
    Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

// ── Field label ────────────────────────────────────────────────────────────

@Composable
fun AuthFieldLabel(text: String) {
    Text(text = text, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground)
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
        placeholder = { Text(placeholder, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
        textStyle = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            errorContainerColor = MaterialTheme.colorScheme.surfaceVariant,
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
                        ), contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant
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
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}

// ── "Can't access? Call Support" footer ───────────────────────────────────

@Composable
fun AuthSupportFooter(onClick: () -> Unit) {
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = MaterialTheme.typography.bodySmall.fontSize
            )
        ) {
            append("Can't access your Account? ")
        }
        withStyle(
            SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = MaterialTheme.typography.bodySmall.fontSize,
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
            tint = if (isMet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, // Using primary for success state if Green500 not in MaterialTheme
            modifier = Modifier
                .size(20.dp)
                .padding(top = 1.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.bodySmall, 
            color = if (isMet) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ── Success/info screen scaffold ───────────────────────────────────────────

@Composable
fun AuthSuccessScaffold(
    title: String,
    subtitle: String,
    buttonLabel: String,
    onContinue: () -> Unit,
    onSupport: () -> Unit,
    topContent: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            topContent()
            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = title, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground, textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
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

// ── Animated check circle ─────────────────────

@Composable
fun AnimatedCheckCircle() {
    val scale = remember { androidx.compose.animation.core.Animatable(0f) }
    androidx.compose.runtime.LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f, animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Box(contentAlignment = Alignment.Center) {
        AuthDecorDot(10.dp, (-80).dp, (-20).dp, 0.25f)
        AuthDecorDot(8.dp, 70.dp, (-50).dp, 0.20f)
        AuthDecorDot(12.dp, (-50).dp, 60.dp, 0.18f)
        AuthDecorDot(7.dp, 90.dp, 30.dp, 0.15f)

        Box(
            modifier = Modifier
                .size(120.dp)
                .scale(scale.value)
                .background(MaterialTheme.colorScheme.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
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
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = alpha), CircleShape)
    )
}
