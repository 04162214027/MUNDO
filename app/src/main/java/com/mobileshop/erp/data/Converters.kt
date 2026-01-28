package com.mobileshop.erp.data

import androidx.room.TypeConverter
import com.mobileshop.erp.data.entity.ProductType
import com.mobileshop.erp.data.entity.TransactionType

class Converters {
    @TypeConverter
    fun fromProductType(type: ProductType): String = type.name

    @TypeConverter
    fun toProductType(value: String): ProductType = ProductType.valueOf(value)

    @TypeConverter
    fun fromTransactionType(type: TransactionType): String = type.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = TransactionType.valueOf(value)
}
