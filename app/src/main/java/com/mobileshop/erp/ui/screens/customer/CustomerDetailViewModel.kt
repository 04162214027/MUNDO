package com.mobileshop.erp.ui.screens.customer

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.entity.CustomerKhata
import com.mobileshop.erp.data.entity.KhataTransaction
import com.mobileshop.erp.data.repository.KhataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CustomerDetailUiState(
    val customer: CustomerKhata? = null,
    val transactions: List<KhataTransaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class CustomerDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val khataRepository: KhataRepository
) : ViewModel() {

    private val customerId: Long = savedStateHandle["customerId"] ?: 0L

    private val _uiState = MutableStateFlow(CustomerDetailUiState())
    val uiState: StateFlow<CustomerDetailUiState> = _uiState.asStateFlow()

    init {
        loadCustomer()
        loadTransactions()
    }

    private fun loadCustomer() {
        viewModelScope.launch {
            val customer = khataRepository.getCustomerById(customerId)
            _uiState.update { it.copy(customer = customer, isLoading = false) }
        }
    }

    private fun loadTransactions() {
        viewModelScope.launch {
            khataRepository.getTransactionHistory(customerId).collect { transactions ->
                _uiState.update { it.copy(transactions = transactions) }
            }
        }
    }

    fun addUdhaar(amount: Double, description: String) {
        viewModelScope.launch {
            try {
                khataRepository.addUdhaar(customerId, amount, description)
                loadCustomer() // Refresh
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun receivePayment(amount: Double, description: String) {
        viewModelScope.launch {
            try {
                khataRepository.receivePayment(customerId, amount, description)
                loadCustomer() // Refresh
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
