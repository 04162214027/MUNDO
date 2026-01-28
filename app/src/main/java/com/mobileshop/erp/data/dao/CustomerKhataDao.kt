package com.mobileshop.erp.data.dao

import androidx.room.*
import com.mobileshop.erp.data.entity.CustomerKhata
import com.mobileshop.erp.data.entity.KhataTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomerKhataDao {
    @Query("SELECT * FROM customer_khata ORDER BY updatedAt DESC")
    fun getAllCustomers(): Flow<List<CustomerKhata>>

    @Query("SELECT * FROM customer_khata WHERE customerName LIKE '%' || :query || '%'")
    fun searchCustomers(query: String): Flow<List<CustomerKhata>>

    @Query("SELECT * FROM customer_khata WHERE id = :id")
    suspend fun getCustomerById(id: Long): CustomerKhata?

    @Query("SELECT SUM(totalUdhaar - totalPaid) FROM customer_khata")
    fun getTotalReceivable(): Flow<Double?>

    @Insert
    suspend fun insert(customer: CustomerKhata): Long

    @Update
    suspend fun update(customer: CustomerKhata)

    @Delete
    suspend fun delete(customer: CustomerKhata)

    @Query("UPDATE customer_khata SET totalUdhaar = totalUdhaar + :amount, updatedAt = :timestamp WHERE id = :customerId")
    suspend fun addUdhaar(customerId: Long, amount: Double, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE customer_khata SET totalPaid = totalPaid + :amount, updatedAt = :timestamp WHERE id = :customerId")
    suspend fun addPayment(customerId: Long, amount: Double, timestamp: Long = System.currentTimeMillis())

    // Khata Transactions
    @Query("SELECT * FROM khata_transactions WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getTransactionsByCustomer(customerId: Long): Flow<List<KhataTransaction>>

    @Insert
    suspend fun insertTransaction(transaction: KhataTransaction): Long
}
