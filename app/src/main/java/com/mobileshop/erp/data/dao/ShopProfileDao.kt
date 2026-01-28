package com.mobileshop.erp.data.dao

import androidx.room.*
import com.mobileshop.erp.data.entity.ShopProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ShopProfileDao {
    @Query("SELECT * FROM shop_profile WHERE id = 1")
    fun getShopProfile(): Flow<ShopProfile?>

    @Query("SELECT * FROM shop_profile WHERE id = 1")
    suspend fun getShopProfileOnce(): ShopProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: ShopProfile)

    @Update
    suspend fun update(profile: ShopProfile)
}
