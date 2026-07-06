package com.taher.beatly.ui.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.theme.BeatlyTheme
import com.taher.beatly.ui.theme.BodyMediumMedium
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray600
import com.taher.beatly.ui.theme.Purple500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White

@Composable
fun PaymentMethodScreen(
    viewModel      : PaymentMethodViewModel = viewModel(),
    onBackClicked  : () -> Unit,
    onAddCard      : () -> Unit,
    onContinue     : () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 120.dp)
        ) {
            SubscriptionTopBar(
                title         = "Payment Method",
                onBackClicked = onBackClicked,
                onMoreClicked = {}
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                uiState.methods.forEach { method ->
                    PaymentMethodRow(
                        method    = method,
                        onSelect  = { viewModel.onMethodSelected(method.id) }
                    )
                }

                // Add New Card
                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .clickable { onAddCard() },
                    shape     = RoundedCornerShape(14.dp),
                    colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier          = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Add New Card", style = BodyMediumMedium, color = TextBlack)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.Add, contentDescription = null, tint = TextBlack, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }

        AuthPrimaryButton(
            text     = "Continue",
            onClick  = onContinue,
            enabled  = uiState.selectedMethodId.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

@Composable
private fun PaymentMethodRow(method: PaymentMethod, onSelect: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
        elevation = CardDefaults.cardElevation(0.dp),
        border    = if (method.isSelected)
            androidx.compose.foundation.BorderStroke(1.5.dp, Purple500) else null
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon per method
            val icon = when (method.id) {
                "paypal"     -> Icons.Default.Payment
                "google_pay" -> Icons.Default.Wallet
                "apple_pay"  -> Icons.Default.PhoneAndroid
                else         -> Icons.Default.CreditCard
            }
            Icon(icon, contentDescription = null, tint = Gray600, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(method.label, style = BodyMediumMedium, color = TextBlack, modifier = Modifier.weight(1f))

            // Check badge
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (method.isSelected) Purple500 else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint     = if (method.isSelected) White else Gray400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PaymentMethodPreview() {
    BeatlyTheme { PaymentMethodScreen(onBackClicked = {}, onAddCard = {}, onContinue = {}) }
}