package com.mobileshop.erp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CustomerKhata::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("productId"), Index("customerId")]
)
data class Sale(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val customerId: Long? = null,
    val productName: String,
    val quantity: Int,
    val purchasePrice: Double,
    val sellingPrice: Double,
    val totalAmount: Double,
    val profit: Double,
    val isUdhaar: Boolean = false, // Credit sale
    val isPaid: Boolean = true,
    val soldAt: Long = System.currentTimeMillis()
)
