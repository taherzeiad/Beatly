package com.taher.beatly.ui.auth.signup

import com.taher.beatly.domain.model.BeatlyResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignUpUiState(
    val email                   : String  = "",
    val username                : String  = "",
    val password                : String  = "",
    val confirmPassword         : String  = "",
    val isPasswordVisible       : Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isTermsAccepted         : Boolean = false,
    val isFormValid             : Boolean = false,
    val isLoading               : Boolean = false,
    val passwordMatchError      : Boolean = false,
    val errorMessage            : String? = null,
    val isSuccess               : Boolean = false
)

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEmailChanged(v: String)           { _uiState.update { it.copy(email = v) };           validate() }
    fun onUsernameChanged(v: String)        { _uiState.update { it.copy(username = v) };        validate() }
    fun onPasswordChanged(v: String)        { _uiState.update { it.copy(password = v) };        validate() }
    fun onConfirmPasswordChanged(v: String) { _uiState.update { it.copy(confirmPassword = v) }; validate() }
    fun onPasswordVisibilityToggled()        { _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }
    fun onConfirmPasswordVisibilityToggled() { _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) } }
    fun onTermsToggled()                    { _uiState.update { it.copy(isTermsAccepted = !it.isTermsAccepted) }; validate() }

    fun onSignUpClicked() {
        val s = _uiState.value
        if (!s.isFormValid) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.signUp(s.email.trim(), s.password, s.username.trim())) {
                is BeatlyResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is BeatlyResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> {}
            }
        }
    }

    private fun validate() {
        val s      = _uiState.value
        val match  = s.password == s.confirmPassword && s.confirmPassword.isNotEmpty()
        _uiState.update { it.copy(
            passwordMatchError = s.confirmPassword.isNotEmpty() && !match,
            isFormValid        = s.email.isNotBlank() && s.username.isNotBlank()
                    && s.password.length >= 6 && match && s.isTermsAccepted
        )}
    }
}