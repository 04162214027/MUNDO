package com.mobileshop.erp.data.dao

import androidx.room.*
import com.mobileshop.erp.data.entity.OldPhonePurchase
import kotlinx.coroutines.flow.Flow

@Dao
interface OldPhonePurchaseDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(purchase: OldPhonePurchase): Long
    
    @Update
    suspend fun update(purchase: OldPhonePurchase)
    
    @Delete
    suspend fun delete(purchase: OldPhonePurchase)
    
    @Query("SELECT * FROM old_phone_purchases ORDER BY createdAt DESC")
    fun getAllPurchases(): Flow<List<OldPhonePurchase>>
    
    @Query("SELECT * FROM old_phone_purchases WHERE isSold = 0 ORDER BY createdAt DESC")
    fun getUnsoldPurchases(): Flow<List<OldPhonePurchase>>
    
    @Query("SELECT * FROM old_phone_purchases WHERE isSold = 1 ORDER BY soldAt DESC")
    fun getSoldPurchases(): Flow<List<OldPhonePurchase>>
    
    @Query("SELECT * FROM old_phone_purchases WHERE id = :id")
    suspend fun getPurchaseById(id: Long): OldPhonePurchase?
    
    @Query("SELECT * FROM old_phone_purchases WHERE imeiNumber = :imei")
    suspend fun getPurchaseByImei(imei: String): OldPhonePurchase?
    
    @Query("SELECT * FROM old_phone_purchases WHERE sellerName LIKE '%' || :query || '%' OR mobileModel LIKE '%' || :query || '%' OR imeiNumber LIKE '%' || :query || '%'")
    fun searchPurchases(query: String): Flow<List<OldPhonePurchase>>
    
    @Query("UPDATE old_phone_purchases SET isSold = 1, soldPrice = :soldPrice, soldAt = :soldAt WHERE id = :id")
    suspend fun markAsSold(id: Long, soldPrice: Double, soldAt: Long = System.currentTimeMillis())
    
    @Query("SELECT SUM(purchasePrice) FROM old_phone_purchases")
    fun getTotalPurchaseAmount(): Flow<Double?>
    
    @Query("SELECT SUM(soldPrice - purchasePrice) FROM old_phone_purchases WHERE isSold = 1")
    fun getTotalProfit(): Flow<Double?>
    
    @Query("SELECT COUNT(*) FROM old_phone_purchases WHERE isSold = 0")
    fun getUnsoldCount(): Flow<Int>
}
