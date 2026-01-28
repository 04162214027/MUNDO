package com.mobileshop.erp.ui.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.entity.CustomerKhata
import com.mobileshop.erp.data.entity.Product
import com.mobileshop.erp.data.entity.ProductType
import com.mobileshop.erp.data.repository.KhataRepository
import com.mobileshop.erp.data.repository.ProductRepository
import com.mobileshop.erp.data.repository.SaleRepository
import com.mobileshop.erp.data.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardStats(
    val totalProfit: Double = 0.0,
    val totalUdhaar: Double = 0.0,
    val cashFlow: Double = 0.0,
    val totalStock: Int = 0
)

data class MainUiState(
    val shopName: String = "",
    val ownerName: String = "",
    val stats: DashboardStats = DashboardStats(),
    val customers: List<CustomerKhata> = emptyList(),
    val handsets: List<Product> = emptyList(),
    val accessories: List<Product> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true,
    val currentPage: Int = 0
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val securePreferences: SecurePreferences,
    private val productRepository: ProductRepository,
    private val khataRepository: KhataRepository,
    private val saleRepository: SaleRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadShopDetails()
        observeData()
    }

    private fun loadShopDetails() {
        _uiState.update { 
            it.copy(
                shopName = securePreferences.getShopName(),
                ownerName = securePreferences.getOwnerName()
            )
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            // Combine all data streams
            combine(
                saleRepository.getTotalProfit(),
                khataRepository.getTotalReceivable(),
                saleRepository.getTotalCashReceived(),
                productRepository.getTotalStockCount()
            ) { profit, udhaar, cash, stock ->
                DashboardStats(
                    totalProfit = profit ?: 0.0,
                    totalUdhaar = udhaar ?: 0.0,
                    cashFlow = cash ?: 0.0,
                    totalStock = stock
                )
            }.collect { stats ->
                _uiState.update { it.copy(stats = stats, isLoading = false) }
            }
        }

        viewModelScope.launch {
            _searchQuery.flatMapLatest { query ->
                if (query.isBlank()) {
                    khataRepository.getAllCustomers()
                } else {
                    khataRepository.searchCustomers(query)
                }
            }.collect { customers ->
                _uiState.update { it.copy(customers = customers) }
            }
        }

        viewModelScope.launch {
            productRepository.getHandsets().collect { handsets ->
                _uiState.update { it.copy(handsets = handsets) }
            }
        }

        viewModelScope.launch {
            productRepository.getAccessories().collect { accessories ->
                _uiState.update { it.copy(accessories = accessories) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setCurrentPage(page: Int) {
        _uiState.update { it.copy(currentPage = page) }
    }

    fun addCustomer(name: String, phone: String) {
        viewModelScope.launch {
            khataRepository.addCustomer(
                CustomerKhata(
                    customerName = name,
                    phoneNumber = phone
                )
            )
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun deleteCustomer(customer: CustomerKhata) {
        viewModelScope.launch {
            khataRepository.deleteCustomer(customer)
        }
    }
}
