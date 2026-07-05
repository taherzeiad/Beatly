package com.taher.beatly.ui.auth.signup

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class SignUpUiState(
    val email                  : String  = "",
    val username               : String  = "",
    val password               : String  = "",
    val confirmPassword        : String  = "",
    val isPasswordVisible      : Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isTermsAccepted        : Boolean = false,
    val isFormValid            : Boolean = false,
    val isLoading              : Boolean = false,
    val passwordMatchError     : Boolean = false,
    val errorMessage           : String? = null
)

@HiltViewModel
class SignUpViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
        validate()
    }

    fun onUsernameChanged(value: String) {
        _uiState.update { it.copy(username = value) }
        validate()
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value) }
        validate()
    }

    fun onConfirmPasswordChanged(value: String) {
        _uiState.update { it.copy(confirmPassword = value) }
        validate()
    }

    fun onPasswordVisibilityToggled() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onConfirmPasswordVisibilityToggled() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }

    fun onTermsToggled() {
        _uiState.update { it.copy(isTermsAccepted = !it.isTermsAccepted) }
        validate()
    }

    fun onSignUpClicked() {
        _uiState.update { it.copy(isLoading = true) }
        // TODO: call auth repository
    }

    private fun validate() {
        val s = _uiState.value
        val passwordsMatch = s.password == s.confirmPassword
        _uiState.update {
            it.copy(
                passwordMatchError = s.confirmPassword.isNotEmpty() && !passwordsMatch,
                isFormValid = s.email.isNotBlank()
                        && s.username.isNotBlank()
                        && s.password.length >= 6
                        && passwordsMatch
                        && s.isTermsAccepted
            )
        }
    }
}