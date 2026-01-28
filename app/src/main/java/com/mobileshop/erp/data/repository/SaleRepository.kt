package com.mobileshop.erp.data.repository

import com.mobileshop.erp.data.dao.SaleDao
import com.mobileshop.erp.data.entity.Sale
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaleRepository @Inject constructor(
    private val saleDao: SaleDao
) {
    fun getAllSales(): Flow<List<Sale>> = saleDao.getAllSales()

    fun getSalesByCustomer(customerId: Long): Flow<List<Sale>> = 
        saleDao.getSalesByCustomer(customerId)

    suspend fun getSaleById(id: Long): Sale? = saleDao.getSaleById(id)

    fun getTotalProfit(): Flow<Double?> = saleDao.getTotalProfit()

    fun getTotalCashReceived(): Flow<Double?> = saleDao.getTotalCashReceived()

    fun getTotalPendingUdhaar(): Flow<Double?> = saleDao.getTotalPendingUdhaar()

    fun getTotalRevenue(): Flow<Double?> = saleDao.getTotalRevenue()

    suspend fun markSaleAsPaid(saleId: Long) = saleDao.markAsPaid(saleId)

    suspend fun deleteSale(sale: Sale) = saleDao.delete(sale)
}
