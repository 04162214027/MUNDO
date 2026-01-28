package com.mobileshop.erp.data.dao

import androidx.room.*
import com.mobileshop.erp.data.entity.Sale
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleDao {
    @Query("SELECT * FROM sales ORDER BY soldAt DESC")
    fun getAllSales(): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE customerId = :customerId ORDER BY soldAt DESC")
    fun getSalesByCustomer(customerId: Long): Flow<List<Sale>>

    @Query("SELECT * FROM sales WHERE id = :id")
    suspend fun getSaleById(id: Long): Sale?

    @Query("SELECT SUM(profit) FROM sales")
    fun getTotalProfit(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE isPaid = 1")
    fun getTotalCashReceived(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM sales WHERE isUdhaar = 1 AND isPaid = 0")
    fun getTotalPendingUdhaar(): Flow<Double?>

    @Query("SELECT SUM(totalAmount) FROM sales")
    fun getTotalRevenue(): Flow<Double?>

    @Insert
    suspend fun insert(sale: Sale): Long

    @Update
    suspend fun update(sale: Sale)

    @Delete
    suspend fun delete(sale: Sale)

    @Query("UPDATE sales SET isPaid = 1 WHERE id = :saleId")
    suspend fun markAsPaid(saleId: Long)
}
