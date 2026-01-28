package com.mobileshop.erp.data.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecurePreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val securePrefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "mobile_shop_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    companion object {
        private const val KEY_PIN = "user_pin"
        private const val KEY_SETUP_COMPLETED = "setup_completed"
        private const val KEY_SHOP_NAME = "shop_name"
        private const val KEY_OWNER_NAME = "owner_name"
    }

    fun savePin(pin: String) {
        securePrefs.edit().putString(KEY_PIN, pin).apply()
    }

    fun getPin(): String? = securePrefs.getString(KEY_PIN, null)

    fun verifyPin(enteredPin: String): Boolean {
        return getPin() == enteredPin
    }

    fun isSetupCompleted(): Boolean {
        return securePrefs.getBoolean(KEY_SETUP_COMPLETED, false)
    }

    fun markSetupCompleted() {
        securePrefs.edit().putBoolean(KEY_SETUP_COMPLETED, true).apply()
    }

    fun saveShopDetails(shopName: String, ownerName: String) {
        securePrefs.edit()
            .putString(KEY_SHOP_NAME, shopName)
            .putString(KEY_OWNER_NAME, ownerName)
            .apply()
    }

    fun getShopName(): String = securePrefs.getString(KEY_SHOP_NAME, "") ?: ""

    fun getOwnerName(): String = securePrefs.getString(KEY_OWNER_NAME, "") ?: ""

    fun updateShopName(shopName: String) {
        securePrefs.edit().putString(KEY_SHOP_NAME, shopName).apply()
    }

    fun updateOwnerName(ownerName: String) {
        securePrefs.edit().putString(KEY_OWNER_NAME, ownerName).apply()
    }

    fun updatePin(newPin: String) {
        securePrefs.edit().putString(KEY_PIN, newPin).apply()
    }

    fun clearAllData() {
        securePrefs.edit().clear().apply()
    }
}
