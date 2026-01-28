package com.mobileshop.erp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobileshop.erp.data.entity.Sale
import com.mobileshop.erp.data.repository.SaleRepository
import com.mobileshop.erp.data.security.SecurePreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class TransactionHistoryUiState(
    val allSales: List<Sale> = emptyList(),
    val filteredSales: List<Sale> = emptyList(),
    val imeiQuery: String = "",
    val fromDate: Long? = null,
    val toDate: Long? = null,
    val shopName: String = "",
    val isLoading: Boolean = true,
    val hasActiveFilters: Boolean = false
)

@HiltViewModel
class TransactionHistoryViewModel @Inject constructor(
    private val saleRepository: SaleRepository,
    private val securePreferences: SecurePreferences
) : ViewModel() {

    private val _uiState = MutableStateFlow(TransactionHistoryUiState())
    val uiState: StateFlow<TransactionHistoryUiState> = _uiState.asStateFlow()

    init {
        loadShopName()
        loadSales()
    }

    private fun loadShopName() {
        _uiState.update { it.copy(shopName = securePreferences.getShopName()) }
    }

    private fun loadSales() {
        viewModelScope.launch {
            saleRepository.getAllSales().collect { sales ->
                _uiState.update { 
                    it.copy(
                        allSales = sales,
                        filteredSales = applyFilters(sales, it.imeiQuery, it.fromDate, it.toDate),
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateImeiQuery(query: String) {
        _uiState.update { state ->
            val filtered = applyFilters(state.allSales, query, state.fromDate, state.toDate)
            state.copy(
                imeiQuery = query,
                filteredSales = filtered,
                hasActiveFilters = query.isNotEmpty() || state.fromDate != null || state.toDate != null
            )
        }
    }

    fun updateFromDate(date: Long) {
        _uiState.update { state ->
            val filtered = applyFilters(state.allSales, state.imeiQuery, date, state.toDate)
            state.copy(
                fromDate = date,
                filteredSales = filtered,
                hasActiveFilters = state.imeiQuery.isNotEmpty() || true
            )
        }
    }

    fun updateToDate(date: Long) {
        _uiState.update { state ->
            val filtered = applyFilters(state.allSales, state.imeiQuery, state.fromDate, date)
            state.copy(
                toDate = date,
                filteredSales = filtered,
                hasActiveFilters = state.imeiQuery.isNotEmpty() || state.fromDate != null || true
            )
        }
    }

    fun clearFilters() {
        _uiState.update { state ->
            state.copy(
                imeiQuery = "",
                fromDate = null,
                toDate = null,
                filteredSales = state.allSales,
                hasActiveFilters = false
            )
        }
    }

    private fun applyFilters(
        sales: List<Sale>,
        imeiQuery: String,
        fromDate: Long?,
        toDate: Long?
    ): List<Sale> {
        return sales.filter { sale ->
            // IMEI/Name filter
            val matchesImei = if (imeiQuery.isNotEmpty()) {
                sale.productName.contains(imeiQuery, ignoreCase = true)
            } else true

            // Date range filter
            val matchesDateRange = when {
                fromDate != null && toDate != null -> sale.soldAt in fromDate..toDate
                fromDate != null -> sale.soldAt >= fromDate
                toDate != null -> sale.soldAt <= toDate
                else -> true
            }

            matchesImei && matchesDateRange
        }
    }
}
