package com.mobileshop.erp.data.repository

import com.mobileshop.erp.data.dao.OldPhonePurchaseDao
import com.mobileshop.erp.data.entity.OldPhonePurchase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OldPhonePurchaseRepository @Inject constructor(
    private val oldPhonePurchaseDao: OldPhonePurchaseDao
) {
    fun getAllPurchases(): Flow<List<OldPhonePurchase>> = oldPhonePurchaseDao.getAllPurchases()
    
    fun getUnsoldPurchases(): Flow<List<OldPhonePurchase>> = oldPhonePurchaseDao.getUnsoldPurchases()
    
    fun getSoldPurchases(): Flow<List<OldPhonePurchase>> = oldPhonePurchaseDao.getSoldPurchases()
    
    suspend fun getPurchaseById(id: Long): OldPhonePurchase? = oldPhonePurchaseDao.getPurchaseById(id)
    
    suspend fun getPurchaseByImei(imei: String): OldPhonePurchase? = oldPhonePurchaseDao.getPurchaseByImei(imei)
    
    fun searchPurchases(query: String): Flow<List<OldPhonePurchase>> = oldPhonePurchaseDao.searchPurchases(query)
    
    suspend fun insertPurchase(purchase: OldPhonePurchase): Long = oldPhonePurchaseDao.insert(purchase)
    
    suspend fun updatePurchase(purchase: OldPhonePurchase) = oldPhonePurchaseDao.update(purchase)
    
    suspend fun deletePurchase(purchase: OldPhonePurchase) = oldPhonePurchaseDao.delete(purchase)
    
    suspend fun markAsSold(id: Long, soldPrice: Double) = oldPhonePurchaseDao.markAsSold(id, soldPrice)
    
    fun getTotalPurchaseAmount(): Flow<Double?> = oldPhonePurchaseDao.getTotalPurchaseAmount()
    
    fun getTotalProfit(): Flow<Double?> = oldPhonePurchaseDao.getTotalProfit()
    
    fun getUnsoldCount(): Flow<Int> = oldPhonePurchaseDao.getUnsoldCount()
}
