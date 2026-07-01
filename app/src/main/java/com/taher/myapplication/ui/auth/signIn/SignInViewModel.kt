package com.beatly.ui.auth

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SignInUiState(
    val email             : String  = "",
    val password          : String  = "",
    val rememberMe        : Boolean = false,
    val isPasswordVisible : Boolean = false,
    val isFormValid       : Boolean = false,
    val isLoading         : Boolean = false,
    val errorMessage      : String? = null
)

class SignInViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
        validateForm()
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
        validateForm()
    }

    fun onRememberMeToggled() {
        _uiState.update { it.copy(rememberMe = !it.rememberMe) }
    }

    fun onPasswordVisibilityToggled() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onSignInClicked() {
        // TODO: plug in your auth repository here
        _uiState.update { it.copy(isLoading = true) }
    }

    private fun validateForm() {
        val s = _uiState.value
        _uiState.update { it.copy(isFormValid = s.email.isNotBlank() && s.password.length >= 6) }
    }
}