package com.mobileshop.erp.data.dao

import androidx.room.*
import com.mobileshop.erp.data.entity.Product
import com.mobileshop.erp.data.entity.ProductType
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products WHERE isSold = 0 ORDER BY createdAt DESC")
    fun getAllAvailableProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE type = :type AND isSold = 0 ORDER BY createdAt DESC")
    fun getProductsByType(type: ProductType): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE imeiNumber = :imei AND isSold = 0")
    suspend fun getProductByImei(imei: String): Product?

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' AND isSold = 0")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("UPDATE products SET isSold = 1, quantity = quantity - :soldQty WHERE id = :productId")
    suspend fun markAsSold(productId: Long, soldQty: Int)

    @Query("SELECT COUNT(*) FROM products WHERE isSold = 0")
    fun getTotalStockCount(): Flow<Int>

    @Query("SELECT SUM((sellingPrice - purchasePrice) * quantity) FROM products WHERE isSold = 0")
    fun getPotentialProfit(): Flow<Double?>
}
