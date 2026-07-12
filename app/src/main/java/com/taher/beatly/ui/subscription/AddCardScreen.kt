package com.taher.beatly.ui.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.components.AuthFieldLabel
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.components.AuthTextField
import com.taher.beatly.ui.components.RoundIconButton
import com.taher.beatly.ui.theme.*

@Composable
fun AddCardScreen(
    viewModel    : AddCardViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onAddCard    : () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 56.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                RoundIconButton(Icons.Default.ArrowBackIosNew, onBackClicked, "Back")
                Text("Add New Card", style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold), color = TextBlack)
                RoundIconButton(Icons.Default.CropFree, {}, "Scan")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Card preview ───────────────────────────────────────────────
            CreditCardPreview(card = uiState.card, modifier = Modifier.padding(horizontal = 20.dp))

            Spacer(modifier = Modifier.height(28.dp))

            // ── Fields ─────────────────────────────────────────────────────
            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    AuthFieldLabel("Card Name")
                    Spacer(modifier = Modifier.height(8.dp))
                    AuthTextField(value = uiState.card.cardName, onValueChange = viewModel::onCardNameChanged, placeholder = "Jenny Wilson")
                }
                Column {
                    AuthFieldLabel("Card Number")
                    Spacer(modifier = Modifier.height(8.dp))
                    AuthTextField(value = uiState.card.cardNumber, onValueChange = viewModel::onCardNumberChanged, placeholder = "4556 - 5642 - 0695 - 5168", keyboardType = KeyboardType.Number)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        AuthFieldLabel("Expiry Date")
                        Spacer(modifier = Modifier.height(8.dp))
                        AuthTextField(value = uiState.card.expiryDate, onValueChange = viewModel::onExpiryChanged, placeholder = "09/07/26", keyboardType = KeyboardType.Number)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        AuthFieldLabel("CVV")
                        Spacer(modifier = Modifier.height(8.dp))
                        AuthTextField(value = uiState.card.cvv, onValueChange = viewModel::onCvvChanged, placeholder = "355", keyboardType = KeyboardType.NumberPassword, isPassword = true, isPasswordVisible = false)
                    }
                }
            }
        }

        AuthPrimaryButton(
            text     = "Add Now",
            onClick  = onAddCard,
            enabled  = uiState.card.isValid,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

@Composable
private fun CreditCardPreview(card: CardDetails, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(190.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF7A5AF8), Color(0xFF9B7FFF))))
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Credit", style = BodySmallRegular, color = White.copy(alpha = 0.8f))
                Text("VISA", style = BodyMediumMedium.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold), color = White)
            }
            Column {
                Text(
                    text  = card.cardName.ifBlank { "Jenny Wilson" },
                    style = BodyMediumMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text  = card.cardNumber.ifBlank { "4556 - 5642 - 0695 - 5168" },
                        style = BodySmallRegular,
                        color = White.copy(alpha = 0.9f)
                    )
                    Text(
                        text  = card.expiryDate.ifBlank { "04/26" },
                        style = BodySmallRegular,
                        color = White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddCardPreview() {
    BeatlyTheme {
        val vm = AddCardViewModel().apply {
            onCardNameChanged("Jenny Wilson")
            onCardNumberChanged("4556564206955168")
            onExpiryChanged("09/07/26")
            onCvvChanged("355")
        }
        AddCardScreen(vm, onBackClicked = {}, onAddCard = {})
    }
}