package com.dusty.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dusty.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RegisterState(
    val displayName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class RegisterViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onDisplayNameChanged(name: String) {
        _state.value = _state.value.copy(displayName = name, error = null)
    }

    fun onEmailChanged(email: String) {
        _state.value = _state.value.copy(email = email, error = null)
    }

    fun onPasswordChanged(password: String) {
        _state.value = _state.value.copy(password = password, error = null)
    }

    fun onConfirmPasswordChanged(password: String) {
        _state.value = _state.value.copy(confirmPassword = password, error = null)
    }

    fun signUp() {
        val current = _state.value
        when {
            current.displayName.isBlank() || current.email.isBlank() || current.password.isBlank() -> {
                _state.value = current.copy(error = "Please fill in all fields")
                return
            }
            current.password != current.confirmPassword -> {
                _state.value = current.copy(error = "Passwords do not match")
                return
            }
            current.password.length < 6 -> {
                _state.value = current.copy(error = "Password must be at least 6 characters")
                return
            }
        }
        viewModelScope.launch {
            _state.value = current.copy(isLoading = true, error = null)
            try {
                authRepository.signUp(current.email.trim(), current.password, current.displayName.trim())
                _state.value = _state.value.copy(isLoading = false, isSuccess = true)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed"
                )
            }
        }
    }
}
