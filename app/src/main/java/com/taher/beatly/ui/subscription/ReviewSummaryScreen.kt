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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.theme.*

@Composable
fun ReviewSummaryScreen(
    viewModel    : ReviewSummaryViewModel = viewModel(),
    onBackClicked: () -> Unit,
    onConfirm    : () -> Unit,
    onChangeMethod: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            SubscriptionTopBar(
                title         = "Review Summary",
                onBackClicked = onBackClicked,
                onMoreClicked = {}
            )

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier            = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // ── Selected plan card ─────────────────────────────────────
                uiState.plan?.let { plan ->
                    Card(
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
                        elevation = CardDefaults.cardElevation(0.dp),
                        border    = androidx.compose.foundation.BorderStroke(1.5.dp, Purple500)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier              = Modifier.fillMaxWidth(),
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = Gray600, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(plan.type, style = BodySmallRegular, color = Gray600)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(Purple500, RoundedCornerShape(8.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = White, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("USD ${plan.monthlyPrice.toInt()}\$/Month", style = Headline, color = TextBlack)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(plan.description, style = BodySmallRegular, color = Gray500)
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("See benefits", style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold), color = TextBlack)
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextBlack, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }

                // ── Summary table ──────────────────────────────────────────
                Column {
                    Text("Summary", style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold), color = TextBlack)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SummaryRow("Amount", "\$${uiState.plan?.monthlyPrice?.let { String.format("%.2f", it) } ?: "0.00"}")
                            Spacer(modifier = Modifier.height(8.dp))
                            SummaryRow("Tax",    "\$${String.format("%.2f", uiState.tax)}")
                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = Gray200, thickness = 1.dp)
                            SummaryRow("Total",  "\$${String.format("%.2f", uiState.total)}", bold = true)
                        }
                    }
                }

                // ── Payment By ────────────────────────────────────────────
                Column {
                    Text("Payment By", style = BodyMediumMedium.copy(fontWeight = FontWeight.Bold), color = TextBlack)
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        shape     = RoundedCornerShape(16.dp),
                        colors    = CardDefaults.cardColors(containerColor = SurfaceFill),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Row(
                            modifier          = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Payment, contentDescription = null, tint = Gray600, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(uiState.paymentMethod?.label ?: "", style = BodyMediumMedium, color = TextBlack, modifier = Modifier.weight(1f))
                            TextButton(onClick = onChangeMethod, contentPadding = PaddingValues(0.dp)) {
                                Text("Change", style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold), color = Purple500)
                            }
                        }
                    }
                }
            }
        }

        AuthPrimaryButton(
            text     = "Confirm payment",
            onClick  = { viewModel.onConfirmPayment(); onConfirm() },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

@Composable
private fun SummaryRow(label: String, value: String, bold: Boolean = false) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(label, style = if (bold) BodyMediumMedium.copy(fontWeight = FontWeight.Bold) else BodySmallRegular, color = TextBlack)
        Text(value, style = if (bold) BodyMediumMedium.copy(fontWeight = FontWeight.Bold) else BodySmallRegular, color = TextBlack)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ReviewSummaryPreview() {
    BeatlyTheme { ReviewSummaryScreen(onBackClicked = {}, onConfirm = {}, onChangeMethod = {}) }
}