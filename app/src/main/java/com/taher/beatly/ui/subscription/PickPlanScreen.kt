package com.taher.beatly.ui.subscription

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.taher.beatly.ui.components.AuthPrimaryButton
import com.taher.beatly.ui.theme.BeatlyTheme
import com.taher.beatly.ui.theme.BodySmallRegular
import com.taher.beatly.ui.theme.Gray400
import com.taher.beatly.ui.theme.Gray500
import com.taher.beatly.ui.theme.Gray600
import com.taher.beatly.ui.theme.Headline
import com.taher.beatly.ui.theme.Purple500
import com.taher.beatly.ui.theme.SurfaceFill
import com.taher.beatly.ui.theme.TextBlack
import com.taher.beatly.ui.theme.White

@Composable
fun PickPlanScreen(
    viewModel: PickPlanViewModel = hiltViewModel(),
    onBackClicked: () -> Unit,
    onContinue: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PickPlanContent(
        uiState = uiState,
        onBackClicked = onBackClicked,
        onBillingCycleChange = viewModel::onBillingCycleChanged,
        onPlanSelected = viewModel::onPlanSelected,
        onBenefitsToggled = viewModel::onBenefitsToggled,
        onContinue = onContinue
    )
}

@Composable
private fun PickPlanContent(
    uiState: PickPlanUiState,
    onBackClicked: () -> Unit,
    onBillingCycleChange: (BillingCycle) -> Unit,
    onPlanSelected: (String) -> Unit,
    onBenefitsToggled: (String) -> Unit,
    onContinue: () -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(White)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ) {
            // ── Top bar ────────────────────────────────────────────────────
            SubscriptionTopBar(title = "Pick Your Plan", onBackClicked = onBackClicked)

            Spacer(modifier = Modifier.height(20.dp))

            // ── Billing cycle toggle ───────────────────────────────────────
            BillingCycleToggle(
                selected = uiState.billingCycle,
                onChange = onBillingCycleChange,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Plan cards ─────────────────────────────────────────────────
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                uiState.plans.forEach { plan ->
                    PlanCard(
                        plan = plan,
                        billingCycle = uiState.billingCycle,
                        onSelect = { onPlanSelected(plan.id) },
                        onBenefitsToggle = { onBenefitsToggled(plan.id) }
                    )
                }
            }
        }

        // ── Pinned Continue ────────────────────────────────────────────────
        AuthPrimaryButton(
            text = "Continue",
            onClick = onContinue,
            enabled = uiState.selectedPlanId.isNotEmpty(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 40.dp)
        )
    }
}

@Composable
private fun BillingCycleToggle(
    selected: BillingCycle,
    onChange: (BillingCycle) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(50))
            .background(SurfaceFill)
            .padding(4.dp)
    ) {
        listOf(
            BillingCycle.MONTHLY to "Monthly",
            BillingCycle.ANNUALLY to "Annually"
        ).forEach { (cycle, label) ->
            val isActive = selected == cycle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(50))
                    .background(if (isActive) Purple500 else SurfaceFill)
                    .clickable { onChange(cycle) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold),
                    color = if (isActive) White else Gray500
                )
            }
        }
    }
}

@Composable
private fun PlanCard(
    plan: PlanOption,
    billingCycle: BillingCycle,
    onSelect: () -> Unit,
    onBenefitsToggle: () -> Unit
) {
    val price = if (billingCycle == BillingCycle.MONTHLY)
        "USD ${plan.monthlyPrice.toInt()}/Month"
    else
        "USD ${plan.yearlyPrice.toInt()}/Year"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceFill),
        elevation = CardDefaults.cardElevation(0.dp),
        border = if (plan.isSelected)
            BorderStroke(1.5.dp, Purple500)
        else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Plan type icon + label
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (plan.type) {
                        "Student" -> Icons.Default.School
                        "Individual" -> Icons.Default.Person
                        else -> Icons.Default.Group
                    }
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Gray600,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(plan.type, style = BodySmallRegular, color = Gray600)
                }
                // Check badge
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (plan.isSelected) Purple500 else SurfaceFill),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = if (plan.isSelected) White else Gray400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                price,
                style = Headline.copy(
                    fontSize = androidx.compose.ui.unit.TextUnit(
                        22f,
                        androidx.compose.ui.unit.TextUnitType.Sp
                    )
                ),
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(plan.description, style = BodySmallRegular, color = Gray500)
            Spacer(modifier = Modifier.height(10.dp))

            // See benefits row
            Row(
                modifier = Modifier.clickable { onBenefitsToggle() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "See benefits",
                    style = BodySmallRegular.copy(fontWeight = FontWeight.SemiBold),
                    color = TextBlack
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (plan.isBenefitsExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TextBlack,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PickPlanMonthlyPreview() {
    BeatlyTheme { PickPlanScreen(onBackClicked = {}, onContinue = {}) }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PickPlanAnnuallyPreview() {
    BeatlyTheme {
        val vm = PickPlanViewModel().apply { onBillingCycleChanged(BillingCycle.ANNUALLY) }
        PickPlanScreen(vm, onBackClicked = {}, onContinue = {})
    }
}