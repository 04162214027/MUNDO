package com.mobileshop.erp.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType {
    UDHAAR_GIVEN,    // Credit given to customer
    PAYMENT_RECEIVED // Payment received from customer
}

@Entity(
    tableName = "khata_transactions",
    foreignKeys = [
        ForeignKey(
            entity = CustomerKhata::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class KhataTransaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val customerId: Long,
    val type: TransactionType,
    val amount: Double,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
