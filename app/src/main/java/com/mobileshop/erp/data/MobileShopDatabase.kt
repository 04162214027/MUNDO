package com.mobileshop.erp.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mobileshop.erp.data.dao.CustomerKhataDao
import com.mobileshop.erp.data.dao.ProductDao
import com.mobileshop.erp.data.dao.SaleDao
import com.mobileshop.erp.data.dao.ShopProfileDao
import com.mobileshop.erp.data.entity.*

@Database(
    entities = [
        ShopProfile::class,
        Product::class,
        Sale::class,
        CustomerKhata::class,
        KhataTransaction::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MobileShopDatabase : RoomDatabase() {
    abstract fun shopProfileDao(): ShopProfileDao
    abstract fun productDao(): ProductDao
    abstract fun saleDao(): SaleDao
    abstract fun customerKhataDao(): CustomerKhataDao
}
