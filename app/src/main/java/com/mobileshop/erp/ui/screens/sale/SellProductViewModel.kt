package com.mobileshop.erp.ui.screens.sale

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.entity.CustomerKhata
import com.mobileshop.erp.data.entity.Product
import com.mobileshop.erp.data.repository.KhataRepository
import com.mobileshop.erp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SellProductUiState(
    val product: Product? = null,
    val customers: List<CustomerKhata> = emptyList(),
    val selectedCustomerId: Long? = null,
    val sellingPrice: String = "",
    val quantity: String = "1",
    val isUdhaar: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isSold: Boolean = false
)

@HiltViewModel
class SellProductViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val productRepository: ProductRepository,
    private val khataRepository: KhataRepository
) : ViewModel() {

    private val productId: Long = savedStateHandle["productId"] ?: 0L

    private val _uiState = MutableStateFlow(SellProductUiState())
    val uiState: StateFlow<SellProductUiState> = _uiState.asStateFlow()

    init {
        loadProduct()
        loadCustomers()
    }

    private fun loadProduct() {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            product?.let {
                _uiState.update { state ->
                    state.copy(
                        product = it,
                        sellingPrice = it.sellingPrice.toLong().toString(),
                        isLoading = false
                    )
                }
            }
        }
    }

    private fun loadCustomers() {
        viewModelScope.launch {
            khataRepository.getAllCustomers().collect { customers ->
                _uiState.update { it.copy(customers = customers) }
            }
        }
    }

    fun updateSellingPrice(price: String) {
        if (price.isEmpty() || price.all { it.isDigit() || it == '.' }) {
            _uiState.update { it.copy(sellingPrice = price, error = null) }
        }
    }

    fun updateQuantity(qty: String) {
        if (qty.isEmpty() || qty.all { it.isDigit() }) {
            _uiState.update { it.copy(quantity = qty, error = null) }
        }
    }

    fun selectCustomer(customerId: Long?) {
        _uiState.update { it.copy(selectedCustomerId = customerId) }
    }

    fun toggleUdhaar(isUdhaar: Boolean) {
        _uiState.update { it.copy(isUdhaar = isUdhaar) }
    }

    fun sellProduct() {
        val state = _uiState.value
        val product = state.product ?: return

        val sellingPrice = state.sellingPrice.toDoubleOrNull()
        val quantity = state.quantity.toIntOrNull() ?: 1

        when {
            sellingPrice == null || sellingPrice <= 0 -> {
                _uiState.update { it.copy(error = "Please enter a valid selling price") }
                return
            }
            quantity <= 0 -> {
                _uiState.update { it.copy(error = "Quantity must be at least 1") }
                return
            }
            quantity > product.quantity -> {
                _uiState.update { it.copy(error = "Not enough stock (Available: ${product.quantity})") }
                return
            }
            state.isUdhaar && state.selectedCustomerId == null -> {
                _uiState.update { it.copy(error = "Please select a customer for Udhaar sale") }
                return
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                val sale = productRepository.sellProduct(
                    product = product,
                    quantity = quantity,
                    sellingPrice = sellingPrice,
                    customerId = state.selectedCustomerId,
                    isUdhaar = state.isUdhaar
                )

                // If it's Udhaar, add to customer's khata
                if (state.isUdhaar && state.selectedCustomerId != null) {
                    khataRepository.addUdhaar(
                        customerId = state.selectedCustomerId,
                        amount = sale.totalAmount,
                        description = "Sale: ${product.name}"
                    )
                }

                _uiState.update { it.copy(isLoading = false, isSold = true) }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(isLoading = false, error = "Sale failed: ${e.message}") 
                }
            }
        }
    }
}
