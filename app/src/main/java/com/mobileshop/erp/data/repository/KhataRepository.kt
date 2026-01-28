package com.mobileshop.erp.data.repository

import com.mobileshop.erp.data.dao.CustomerKhataDao
import com.mobileshop.erp.data.entity.CustomerKhata
import com.mobileshop.erp.data.entity.KhataTransaction
import com.mobileshop.erp.data.entity.TransactionType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KhataRepository @Inject constructor(
    private val customerKhataDao: CustomerKhataDao
) {
    fun getAllCustomers(): Flow<List<CustomerKhata>> = customerKhataDao.getAllCustomers()

    fun searchCustomers(query: String): Flow<List<CustomerKhata>> = 
        customerKhataDao.searchCustomers(query)

    suspend fun getCustomerById(id: Long): CustomerKhata? = 
        customerKhataDao.getCustomerById(id)

    suspend fun addCustomer(customer: CustomerKhata): Long = 
        customerKhataDao.insert(customer)

    suspend fun updateCustomer(customer: CustomerKhata) = 
        customerKhataDao.update(customer)

    suspend fun deleteCustomer(customer: CustomerKhata) = 
        customerKhataDao.delete(customer)

    suspend fun addUdhaar(customerId: Long, amount: Double, description: String = "") {
        customerKhataDao.addUdhaar(customerId, amount)
        customerKhataDao.insertTransaction(
            KhataTransaction(
                customerId = customerId,
                type = TransactionType.UDHAAR_GIVEN,
                amount = amount,
                description = description
            )
        )
    }

    suspend fun receivePayment(customerId: Long, amount: Double, description: String = "") {
        customerKhataDao.addPayment(customerId, amount)
        customerKhataDao.insertTransaction(
            KhataTransaction(
                customerId = customerId,
                type = TransactionType.PAYMENT_RECEIVED,
                amount = amount,
                description = description
            )
        )
    }

    fun getTransactionHistory(customerId: Long): Flow<List<KhataTransaction>> = 
        customerKhataDao.getTransactionsByCustomer(customerId)

    fun getTotalReceivable(): Flow<Double?> = customerKhataDao.getTotalReceivable()
}
