package com.taher.beatly.ui.auth.forgotpassword

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class ForgotPasswordUiState(
    val email: String = "",
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update {
            it.copy(
                email = value,
                isFormValid = value.isNotBlank() && value.contains("@"),
                errorMessage = null
            )
        }
    }

    fun onContinueClicked() {
        _uiState.update { it.copy(isLoading = true) }
        // TODO: call auth repository → send recovery email
    }
}