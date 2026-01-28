package com.mobileshop.erp.ui.screens.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.dao.ShopProfileDao
import com.mobileshop.erp.data.entity.ShopProfile
import com.mobileshop.erp.data.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SetupUiState(
    val shopName: String = "",
    val ownerName: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSetupComplete: Boolean = false
)

@HiltViewModel
class SetupViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val shopProfileDao: ShopProfileDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(SetupUiState())
    val uiState: StateFlow<SetupUiState> = _uiState.asStateFlow()

    fun updateShopName(name: String) {
        _uiState.value = _uiState.value.copy(shopName = name, error = null)
    }

    fun updateOwnerName(name: String) {
        _uiState.value = _uiState.value.copy(ownerName = name, error = null)
    }

    fun updatePin(pin: String) {
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(pin = pin, error = null)
        }
    }

    fun updateConfirmPin(pin: String) {
        if (pin.length <= 4 && pin.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(confirmPin = pin, error = null)
        }
    }

    fun completeSetup() {
        val state = _uiState.value
        
        when {
            state.shopName.isBlank() || state.ownerName.isBlank() -> {
                _uiState.value = state.copy(error = "Please fill all fields")
                return
            }
            state.pin.length != 4 -> {
                _uiState.value = state.copy(error = "PIN must be 4 digits")
                return
            }
            state.pin != state.confirmPin -> {
                _uiState.value = state.copy(error = "PINs do not match")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            
            try {
                // Save to encrypted preferences
                securePreferences.saveShopDetails(state.shopName.trim(), state.ownerName.trim())
                securePreferences.savePin(state.pin)
                securePreferences.markSetupCompleted()

                // Save to database as well
                shopProfileDao.insertOrUpdate(
                    ShopProfile(
                        shopName = state.shopName.trim(),
                        ownerName = state.ownerName.trim()
                    )
                )

                _uiState.value = state.copy(isLoading = false, isSetupComplete = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Setup failed: ${e.message}"
                )
            }
        }
    }
}
