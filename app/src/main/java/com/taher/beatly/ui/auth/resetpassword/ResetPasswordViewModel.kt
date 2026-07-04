package com.taher.beatly.ui.auth.resetpassword

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class PasswordRule(
    val label: String,
    val isMet: Boolean
)

data class ResetPasswordUiState(
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val passwordRules: List<PasswordRule> = defaultRules(),
    val allRulesMet: Boolean = false,
    val passwordsMatch: Boolean = false,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false
)

private fun defaultRules() = listOf(
    PasswordRule("Use at least 8 characters", false),
    PasswordRule("Use a mix of letters, numbers, and special characters (e.g.: #\$!%)", false),
    PasswordRule("Try combining words and symbols into a unique phrase", false)
)

@HiltViewModel
class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

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

    fun onContinueClicked() {
        _uiState.update { it.copy(isLoading = true) }
        // TODO: call auth repository → reset password
    }

    private fun validate() {
        val s = _uiState.value
        val rules = listOf(
            PasswordRule(
                "Use at least 8 characters",
                s.password.length >= 8
            ),
            PasswordRule(
                "Use a mix of letters, numbers, and special characters (e.g.: #\$!%)",
                s.password.any { it.isLetter() } && s.password.any { it.isDigit() } && s.password.any { !it.isLetterOrDigit() }),
            PasswordRule(
                "Try combining words and symbols into a unique phrase",
                s.password.length >= 12
            )
        )
        val allMet = rules.all { it.isMet }
        val match = s.password == s.confirmPassword && s.confirmPassword.isNotEmpty()
        _uiState.update {
            it.copy(
                passwordRules = rules,
                allRulesMet = allMet,
                passwordsMatch = match,
                isFormValid = allMet && match
            )
        }
    }
}