package com.mobileshop.erp.data.repository

import com.mobileshop.erp.data.dao.ProductDao
import com.mobileshop.erp.data.dao.SaleDao
import com.mobileshop.erp.data.entity.Product
import com.mobileshop.erp.data.entity.ProductType
import com.mobileshop.erp.data.entity.Sale
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val productDao: ProductDao,
    private val saleDao: SaleDao
) {
    fun getAllAvailableProducts(): Flow<List<Product>> = productDao.getAllAvailableProducts()

    fun getHandsets(): Flow<List<Product>> = productDao.getProductsByType(ProductType.HANDSET)

    fun getAccessories(): Flow<List<Product>> = productDao.getProductsByType(ProductType.ACCESSORY)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    suspend fun getProductByImei(imei: String): Product? = productDao.getProductByImei(imei)

    suspend fun addProduct(product: Product): Long = productDao.insert(product)

    suspend fun updateProduct(product: Product) = productDao.update(product)

    suspend fun deleteProduct(product: Product) = productDao.delete(product)

    suspend fun sellProduct(
        product: Product,
        quantity: Int,
        sellingPrice: Double,
        customerId: Long? = null,
        isUdhaar: Boolean = false
    ): Sale {
        val totalAmount = sellingPrice * quantity
        val profit = (sellingPrice - product.purchasePrice) * quantity

        val sale = Sale(
            productId = product.id,
            customerId = customerId,
            productName = product.name,
            quantity = quantity,
            purchasePrice = product.purchasePrice,
            sellingPrice = sellingPrice,
            totalAmount = totalAmount,
            profit = profit,
            isUdhaar = isUdhaar,
            isPaid = !isUdhaar
        )

        saleDao.insert(sale)
        productDao.markAsSold(product.id, quantity)

        return sale
    }

    fun getTotalStockCount(): Flow<Int> = productDao.getTotalStockCount()

    fun getPotentialProfit(): Flow<Double?> = productDao.getPotentialProfit()
}
