package com.mobileshop.erp.di

import android.content.Context
import androidx.room.Room
import com.mobileshop.erp.data.MobileShopDatabase
import com.mobileshop.erp.data.dao.CustomerKhataDao
import com.mobileshop.erp.data.dao.OldPhonePurchaseDao
import com.mobileshop.erp.data.dao.ProductDao
import com.mobileshop.erp.data.dao.SaleDao
import com.mobileshop.erp.data.dao.ShopProfileDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MobileShopDatabase {
        return Room.databaseBuilder(
            context,
            MobileShopDatabase::class.java,
            "mobile_shop_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideShopProfileDao(database: MobileShopDatabase): ShopProfileDao {
        return database.shopProfileDao()
    }

    @Provides
    @Singleton
    fun provideProductDao(database: MobileShopDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideSaleDao(database: MobileShopDatabase): SaleDao {
        return database.saleDao()
    }

    @Provides
    @Singleton
    fun provideCustomerKhataDao(database: MobileShopDatabase): CustomerKhataDao {
        return database.customerKhataDao()
    }

    @Provides
    @Singleton
    fun provideOldPhonePurchaseDao(database: MobileShopDatabase): OldPhonePurchaseDao {
        return database.oldPhonePurchaseDao()
    }
}
