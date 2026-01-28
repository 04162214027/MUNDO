package com.mobileshop.erp.ui.screens.settings

import androidx.lifecycle.ViewModel
import com.mobileshop.erp.data.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class SettingsUiState(
    val shopName: String = "",
    val ownerName: String = "",
    val isEditing: Boolean = false,
    val showChangePinDialog: Boolean = false,
    val message: String? = null
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _uiState.value = SettingsUiState(
            shopName = securePreferences.getShopName(),
            ownerName = securePreferences.getOwnerName()
        )
    }

    fun updateShopName(name: String) {
        _uiState.value = _uiState.value.copy(shopName = name)
    }

    fun updateOwnerName(name: String) {
        _uiState.value = _uiState.value.copy(ownerName = name)
    }

    fun toggleEditing() {
        val state = _uiState.value
        if (state.isEditing) {
            // Save changes
            securePreferences.updateShopName(state.shopName.trim())
            securePreferences.updateOwnerName(state.ownerName.trim())
            _uiState.value = state.copy(
                isEditing = false,
                message = "Settings saved successfully"
            )
        } else {
            _uiState.value = state.copy(isEditing = true)
        }
    }

    fun showChangePinDialog() {
        _uiState.value = _uiState.value.copy(showChangePinDialog = true)
    }

    fun hideChangePinDialog() {
        _uiState.value = _uiState.value.copy(showChangePinDialog = false)
    }

    fun changePin(currentPin: String, newPin: String): Boolean {
        return if (securePreferences.verifyPin(currentPin)) {
            securePreferences.updatePin(newPin)
            _uiState.value = _uiState.value.copy(
                showChangePinDialog = false,
                message = "PIN changed successfully"
            )
            true
        } else {
            false
        }
    }

    fun clearMessage() {
        _uiState.value = _uiState.value.copy(message = null)
    }
}
