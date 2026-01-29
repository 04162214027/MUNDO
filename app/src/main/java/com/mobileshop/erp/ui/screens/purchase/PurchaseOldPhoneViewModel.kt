package com.mobileshop.erp.ui.screens.purchase

import android.graphics.Bitmap
import android.util.Base64
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.entity.OldPhonePurchase
import com.mobileshop.erp.data.repository.OldPhonePurchaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import javax.inject.Inject

data class PurchaseOldPhoneUiState(
    val sellerName: String = "",
    val sellerCnic: String = "",
    val mobileModel: String = "",
    val mobileColor: String = "",
    val purchasePrice: String = "",
    val imeiNumber: String = "",
    val hasBox: Boolean = false,
    val hasCharger: Boolean = false,
    val hasHandsfree: Boolean = false,
    val customAccessory: String = "",
    val signaturePaths: List<List<Offset>> = emptyList(),
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class PurchaseOldPhoneViewModel @Inject constructor(
    private val repository: OldPhonePurchaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseOldPhoneUiState())
    val uiState: StateFlow<PurchaseOldPhoneUiState> = _uiState.asStateFlow()

    fun updateSellerName(value: String) {
        _uiState.update { it.copy(sellerName = value) }
    }

    fun updateSellerCnic(value: String) {
        // Format CNIC: 12345-1234567-1
        val cleaned = value.replace(Regex("[^0-9]"), "")
        val formatted = when {
            cleaned.length <= 5 -> cleaned
            cleaned.length <= 12 -> "${cleaned.take(5)}-${cleaned.drop(5)}"
            else -> "${cleaned.take(5)}-${cleaned.drop(5).take(7)}-${cleaned.drop(12).take(1)}"
        }
        _uiState.update { it.copy(sellerCnic = formatted) }
    }

    fun updateMobileModel(value: String) {
        _uiState.update { it.copy(mobileModel = value) }
    }

    fun updateMobileColor(value: String) {
        _uiState.update { it.copy(mobileColor = value) }
    }

    fun updatePurchasePrice(value: String) {
        val cleaned = value.replace(Regex("[^0-9]"), "")
        _uiState.update { it.copy(purchasePrice = cleaned) }
    }

    fun updateImeiNumber(value: String) {
        val cleaned = value.replace(Regex("[^0-9]"), "").take(15)
        _uiState.update { it.copy(imeiNumber = cleaned) }
    }

    fun updateHasBox(value: Boolean) {
        _uiState.update { it.copy(hasBox = value) }
    }

    fun updateHasCharger(value: Boolean) {
        _uiState.update { it.copy(hasCharger = value) }
    }

    fun updateHasHandsfree(value: Boolean) {
        _uiState.update { it.copy(hasHandsfree = value) }
    }

    fun updateCustomAccessory(value: String) {
        _uiState.update { it.copy(customAccessory = value) }
    }

    fun updateSignaturePaths(paths: List<List<Offset>>) {
        _uiState.update { it.copy(signaturePaths = paths) }
    }

    fun clearSignature() {
        _uiState.update { it.copy(signaturePaths = emptyList()) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun savePurchase() {
        val state = _uiState.value

        // Validation
        when {
            state.sellerName.isBlank() -> {
                _uiState.update { it.copy(error = "Seller name is required") }
                return
            }
            state.sellerCnic.isBlank() -> {
                _uiState.update { it.copy(error = "Seller CNIC is required") }
                return
            }
            state.mobileModel.isBlank() -> {
                _uiState.update { it.copy(error = "Mobile model is required") }
                return
            }
            state.mobileColor.isBlank() -> {
                _uiState.update { it.copy(error = "Mobile color is required") }
                return
            }
            state.purchasePrice.isBlank() -> {
                _uiState.update { it.copy(error = "Purchase price is required") }
                return
            }
            state.imeiNumber.isBlank() -> {
                _uiState.update { it.copy(error = "IMEI number is required") }
                return
            }
            state.imeiNumber.length != 15 -> {
                _uiState.update { it.copy(error = "IMEI must be 15 digits") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Check if IMEI already exists
                val existingPurchase = repository.getPurchaseByImei(state.imeiNumber)
                if (existingPurchase != null) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "A phone with this IMEI already exists!"
                        )
                    }
                    return@launch
                }

                // Convert signature paths to base64 string (simplified storage)
                val signatureData = if (state.signaturePaths.isNotEmpty()) {
                    state.signaturePaths.joinToString("|") { path ->
                        path.joinToString(",") { "${it.x}:${it.y}" }
                    }
                } else null

                val purchase = OldPhonePurchase(
                    sellerName = state.sellerName.trim(),
                    sellerCnic = state.sellerCnic.trim(),
                    mobileModel = state.mobileModel.trim(),
                    mobileColor = state.mobileColor.trim(),
                    purchasePrice = state.purchasePrice.toDoubleOrNull() ?: 0.0,
                    imeiNumber = state.imeiNumber,
                    hasBox = state.hasBox,
                    hasCharger = state.hasCharger,
                    hasHandsfree = state.hasHandsfree,
                    customAccessory = state.customAccessory.ifBlank { null },
                    signatureBase64 = signatureData
                )

                repository.insertPurchase(purchase)
                _uiState.update { it.copy(isLoading = false, isSaved = true) }

            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = "Failed to save: ${e.message}"
                    )
                }
            }
        }
    }
}
