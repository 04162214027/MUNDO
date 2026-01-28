package com.mobileshop.erp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ProductType {
    HANDSET,
    ACCESSORY
}

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val type: ProductType,
    val imeiNumber: String? = null, // Only for handsets
    val purchasePrice: Double,
    val sellingPrice: Double,
    val quantity: Int = 1,
    val isSold: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    val profit: Double
        get() = (sellingPrice - purchasePrice) * quantity
}
