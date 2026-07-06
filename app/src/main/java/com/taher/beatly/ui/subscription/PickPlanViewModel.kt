package com.taher.beatly.ui.subscription

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// ── Models ──────────────────────────────────────────────────────────────────

enum class BillingCycle { MONTHLY, ANNUALLY }

data class PlanOption(
    val id          : String,
    val type        : String,       // "Student", "Individual", "Family"
    val monthlyPrice: Double,
    val yearlyPrice : Double,
    val description : String,
    val isSelected  : Boolean = false,
    val isBenefitsExpanded: Boolean = false
)

data class PaymentMethod(
    val id         : String,
    val label      : String,
    val isSelected : Boolean = false
)

data class CardDetails(
    val cardName   : String = "",
    val cardNumber : String = "",
    val expiryDate : String = "",
    val cvv        : String = "",
    val isValid    : Boolean = false
)

// ── UI States ──────────────────────────────────────────────────────────────

data class PickPlanUiState(
    val billingCycle   : BillingCycle      = BillingCycle.MONTHLY,
    val plans          : List<PlanOption>  = emptyList(),
    val selectedPlanId : String            = ""
)

data class PaymentMethodUiState(
    val methods          : List<PaymentMethod> = emptyList(),
    val selectedMethodId : String              = ""
)

data class AddCardUiState(
    val card : CardDetails = CardDetails()
)

data class ReviewSummaryUiState(
    val plan          : PlanOption?    = null,
    val paymentMethod : PaymentMethod? = null,
    val tax           : Double         = 1.0,
    val isLoading     : Boolean        = false
) {
    val total: Double get() = (plan?.let {
        if (plan.id.contains("student")) 7.0 else 19.0
    } ?: 0.0) + tax
}

// ── ViewModels ─────────────────────────────────────────────────────────────

@HiltViewModel
class PickPlanViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PickPlanUiState())
    val uiState: StateFlow<PickPlanUiState> = _uiState.asStateFlow()

    private val plans = listOf(
        PlanOption("student",    "Student",    7.0,  72.0,  "Better audio quality with ad-free, offline and with your screen off.", isSelected = true),
        PlanOption("individual", "Individual", 19.0, 216.0, "Better audio quality with ad-free, offline and with your screen off."),
        PlanOption("family",     "Family",     25.0, 280.0, "Better audio quality with ad-free, offline and with your screen off."),
    )

    init { _uiState.update { it.copy(plans = plans, selectedPlanId = "student") } }

    fun onBillingCycleChanged(cycle: BillingCycle) {
        _uiState.update { it.copy(billingCycle = cycle) }
    }

    fun onPlanSelected(planId: String) {
        _uiState.update { state ->
            state.copy(
                selectedPlanId = planId,
                plans = state.plans.map { it.copy(isSelected = it.id == planId) }
            )
        }
    }

    fun onBenefitsToggled(planId: String) {
        _uiState.update { state ->
            state.copy(
                plans = state.plans.map { plan ->
                    if (plan.id == planId) plan.copy(isBenefitsExpanded = !plan.isBenefitsExpanded) else plan
                }
            )
        }
    }
}

@HiltViewModel
class PaymentMethodViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PaymentMethodUiState())
    val uiState: StateFlow<PaymentMethodUiState> = _uiState.asStateFlow()

    init {
        _uiState.update { state ->
            state.copy(
                methods = listOf(
                    PaymentMethod("paypal",     "Paypal",     isSelected = true),
                    PaymentMethod("google_pay", "Google Pay"),
                    PaymentMethod("apple_pay",  "Apple Pay"),
                    PaymentMethod("visa",       "Visa Card"),
                ),
                selectedMethodId = "paypal"
            )
        }
    }

    fun onMethodSelected(methodId: String) {
        _uiState.update { state ->
            state.copy(
                selectedMethodId = methodId,
                methods = state.methods.map { it.copy(isSelected = it.id == methodId) }
            )
        }
    }
}

@HiltViewModel
class AddCardViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddCardUiState())
    val uiState: StateFlow<AddCardUiState> = _uiState.asStateFlow()

    fun onCardNameChanged(value: String)   { updateCard { it.copy(cardName   = value) } }
    fun onCardNumberChanged(value: String) { updateCard { it.copy(cardNumber = value.take(19)) } }
    fun onExpiryChanged(value: String)     { updateCard { it.copy(expiryDate = value.take(8)) } }
    fun onCvvChanged(value: String)        { updateCard { it.copy(cvv        = value.take(3)) } }

    private fun updateCard(transform: (CardDetails) -> CardDetails) {
        _uiState.update { state ->
            val updated = transform(state.card)
            state.copy(card = updated.copy(
                isValid = updated.cardName.isNotBlank()
                        && updated.cardNumber.length >= 16
                        && updated.expiryDate.isNotBlank()
                        && updated.cvv.length == 3
            ))
        }
    }
}

@HiltViewModel
class ReviewSummaryViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ReviewSummaryUiState())
    val uiState: StateFlow<ReviewSummaryUiState> = _uiState.asStateFlow()

    init {
        // In a real app these come from a shared flow / SavedStateHandle
        _uiState.update { state ->
            state.copy(
                plan          = PlanOption("student", "Student", 7.0, 72.0, "Better audio quality with ad-free, offline and with your screen off.", isSelected = true),
                paymentMethod = PaymentMethod("paypal", "Paypal", isSelected = true)
            )
        }
    }

    fun onConfirmPayment() { _uiState.update { it.copy(isLoading = true) } }
}