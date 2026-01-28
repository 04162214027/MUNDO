package com.mobileshop.erp.ui.screens.auth

import androidx.lifecycle.ViewModel
import com.mobileshop.erp.data.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class PinAuthUiState(
    val pin: String = "",
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false,
    val attempts: Int = 0
)

@HiltViewModel
class PinAuthViewModel @Inject constructor(
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinAuthUiState())
    val uiState: StateFlow<PinAuthUiState> = _uiState.asStateFlow()

    val shopName: String = securePreferences.getShopName()

    fun updatePin(pin: String) {
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(
                pin = pin,
                isError = false,
                errorMessage = null
            )

            // Auto-verify when 4 digits entered
            if (pin.length == 4) {
                verifyPin()
            }
        }
    }

    fun verifyPin() {
        val currentState = _uiState.value
        
        if (securePreferences.verifyPin(currentState.pin)) {
            _uiState.value = currentState.copy(isAuthenticated = true)
        } else {
            val newAttempts = currentState.attempts + 1
            _uiState.value = currentState.copy(
                pin = "",
                isError = true,
                errorMessage = "Wrong PIN! Try again. ($newAttempts/5)",
                attempts = newAttempts
            )
        }
    }

    fun appendDigit(digit: Char) {
        val current = _uiState.value.pin
        if (current.length < 4) {
            updatePin(current + digit)
        }
    }

    fun deleteDigit() {
        val current = _uiState.value.pin
        if (current.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(
                pin = current.dropLast(1),
                isError = false,
                errorMessage = null
            )
        }
    }

    fun clearPin() {
        _uiState.value = _uiState.value.copy(
            pin = "",
            isError = false,
            errorMessage = null
        )
    }
}
