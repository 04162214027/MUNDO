package com.mobileshop.erp.ui.screens.product

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.entity.Product
import com.mobileshop.erp.data.entity.ProductType
import com.mobileshop.erp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddProductUiState(
    val productType: ProductType = ProductType.HANDSET,
    val name: String = "",
    val imeiNumber: String = "",
    val purchasePrice: String = "",
    val sellingPrice: String = "",
    val quantity: String = "1",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

@HiltViewModel
class AddProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val productType: String = savedStateHandle["productType"] ?: "HANDSET"

    private val _uiState = MutableStateFlow(
        AddProductUiState(
            productType = ProductType.valueOf(productType)
        )
    )
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    fun updateName(name: String) {
        _uiState.value = _uiState.value.copy(name = name, error = null)
    }

    fun updateImei(imei: String) {
        _uiState.value = _uiState.value.copy(imeiNumber = imei, error = null)
    }

    fun updatePurchasePrice(price: String) {
        if (price.isEmpty() || price.all { it.isDigit() || it == '.' }) {
            _uiState.value = _uiState.value.copy(purchasePrice = price, error = null)
        }
    }

    fun updateSellingPrice(price: String) {
        if (price.isEmpty() || price.all { it.isDigit() || it == '.' }) {
            _uiState.value = _uiState.value.copy(sellingPrice = price, error = null)
        }
    }

    fun updateQuantity(qty: String) {
        if (qty.isEmpty() || qty.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(quantity = qty, error = null)
        }
    }

    fun saveProduct() {
        val state = _uiState.value
        
        when {
            state.name.isBlank() -> {
                _uiState.value = state.copy(error = "Product name is required")
                return
            }
            state.productType == ProductType.HANDSET && state.imeiNumber.isBlank() -> {
                _uiState.value = state.copy(error = "IMEI number is required for handsets")
                return
            }
            state.purchasePrice.isBlank() -> {
                _uiState.value = state.copy(error = "Purchase price is required")
                return
            }
            state.sellingPrice.isBlank() -> {
                _uiState.value = state.copy(error = "Selling price is required")
                return
            }
        }

        viewModelScope.launch {
            _uiState.value = state.copy(isLoading = true)
            
            try {
                // Check for duplicate IMEI
                if (state.productType == ProductType.HANDSET) {
                    val existingProduct = productRepository.getProductByImei(state.imeiNumber)
                    if (existingProduct != null) {
                        _uiState.value = state.copy(
                            isLoading = false,
                            error = "A product with this IMEI already exists"
                        )
                        return@launch
                    }
                }

                val product = Product(
                    name = state.name.trim(),
                    type = state.productType,
                    imeiNumber = if (state.productType == ProductType.HANDSET) 
                        state.imeiNumber.trim() else null,
                    purchasePrice = state.purchasePrice.toDoubleOrNull() ?: 0.0,
                    sellingPrice = state.sellingPrice.toDoubleOrNull() ?: 0.0,
                    quantity = if (state.productType == ProductType.ACCESSORY) 
                        state.quantity.toIntOrNull() ?: 1 else 1
                )

                productRepository.addProduct(product)
                _uiState.value = state.copy(isLoading = false, isSaved = true)
            } catch (e: Exception) {
                _uiState.value = state.copy(
                    isLoading = false,
                    error = "Failed to save: ${e.message}"
                )
            }
        }
    }
}
