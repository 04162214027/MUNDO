package com.mobileshop.erp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shop_profile")
data class ShopProfile(
    @PrimaryKey
    val id: Int = 1,
    val shopName: String,
    val ownerName: String,
    val createdAt: Long = System.currentTimeMillis()
)
