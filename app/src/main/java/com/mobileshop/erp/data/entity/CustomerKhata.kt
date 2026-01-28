package com.mobileshop.erp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "customer_khata")
data class CustomerKhata(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerName: String,
    val phoneNumber: String = "",
    val totalUdhaar: Double = 0.0, // Total credit given
    val totalPaid: Double = 0.0,   // Total amount received
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val balance: Double
        get() = totalUdhaar - totalPaid
}
