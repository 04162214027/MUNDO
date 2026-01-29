package com.mobileshop.erp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "old_phone_purchases")
data class OldPhonePurchase(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sellerName: String,
    val sellerCnic: String,
    val mobileModel: String,
    val mobileColor: String,
    val purchasePrice: Double,
    val imeiNumber: String,
    val hasBox: Boolean = false,
    val hasCharger: Boolean = false,
    val hasHandsfree: Boolean = false,
    val customAccessory: String? = null,
    val signatureBase64: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isSold: Boolean = false,
    val soldPrice: Double? = null,
    val soldAt: Long? = null
) {
    val profit: Double
        get() = if (isSold && soldPrice != null) soldPrice - purchasePrice else 0.0
}
