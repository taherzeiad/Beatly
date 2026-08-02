package com.taher.beatly.ui.auth.signIn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taher.beatly.domain.model.BeatlyResult
import com.taher.beatly.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SignInUiState(
    val email             : String  = "",
    val password          : String  = "",
    val rememberMe        : Boolean = false,
    val isPasswordVisible : Boolean = false,
    val isFormValid       : Boolean = false,
    val isLoading         : Boolean = false,
    val errorMessage      : String? = null,
    val isSuccess         : Boolean = false,
)

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignInUiState())
    val uiState: StateFlow<SignInUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { it.copy(email = value, errorMessage = null) }
        validate()
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
        validate()
    }

    fun onRememberMeToggled()        { _uiState.update { it.copy(rememberMe = !it.rememberMe) } }
    fun onPasswordVisibilityToggled(){ _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) } }

    fun onSignInClicked() {
        val state = _uiState.value
        if (!state.isFormValid) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            when (val result = authRepository.signIn(state.email.trim(), state.password)) {
                is BeatlyResult.Success -> _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                is BeatlyResult.Error   -> _uiState.update { it.copy(isLoading = false, errorMessage = result.message) }
                else -> {}
            }
        }
    }

    private fun validate() {
        val s = _uiState.value
        _uiState.update { it.copy(isFormValid = (s.email.isNotBlank() && s.password.length >= 6)) }
    }
}