# Mobile Shop ERP - App Summary

## 📱 Overview
Mobile Shop ERP is a professional Android application designed specifically for mobile phone shop owners in Pakistan. It helps manage daily business operations including inventory tracking, customer credit (Khata) management, sales recording, and profit calculation.

**Target Platform:** Android 15 (API 35)  
**Language:** Kotlin  
**UI Framework:** Jetpack Compose with Material 3  
**Architecture:** MVVM with Hilt Dependency Injection  

---

## 🚀 App Flow

### 1. First Launch - Setup Screen
When the app is opened for the first time:
```
App Launch → Setup Screen → Enter Details → Main Dashboard
```
**Required Information:**
- Shop Name (e.g., "Ali Mobile Store")
- Owner/Person Name
- 4-Digit Security PIN

All data is securely stored using **EncryptedSharedPreferences**.

### 2. Subsequent Launches - PIN Authentication
```
App Launch → PIN Auth Screen → Enter PIN → Main Dashboard
```
- Users must enter their 4-digit PIN to access the app
- Visual feedback with animated PIN dots
- Number pad for easy input

### 3. Main Dashboard - Dual Page Swipe UI
The main screen features a horizontal pager with two pages:

#### Page 1: Khata & Business Dashboard
- **Shop Name** displayed at top
- **Three Interactive Cards:**
  - 💰 Total Profit (Green)
  - 📊 Total Udhaar/Receivable (Red)
  - 💵 Cash Flow (Blue)
- **Searchable Customer List**
- **Add Customer** FAB button

#### Page 2: Inventory Management
- **Tab Selector:**
  - 📱 Handsets (IMEI-based tracking)
  - 🎧 Accessories (Quantity-based tracking)
- **Product Cards** with pricing details
- **Add Stock** FAB button

---

## 💾 Database Schema (Room DB)

### Entities

#### 1. ShopProfile
```kotlin
- id: Int (Primary Key)
- shopName: String
- ownerName: String
- createdAt: Long
```

#### 2. Product
```kotlin
- id: Long (Primary Key, Auto-generate)
- name: String
- type: ProductType (HANDSET / ACCESSORY)
- imeiNumber: String? (Only for handsets)
- purchasePrice: Double
- sellingPrice: Double
- quantity: Int
- isSold: Boolean
- createdAt: Long
```

#### 3. Sale
```kotlin
- id: Long (Primary Key, Auto-generate)
- productId: Long (Foreign Key)
- customerId: Long? (Foreign Key)
- productName: String
- quantity: Int
- purchasePrice: Double
- sellingPrice: Double
- totalAmount: Double
- profit: Double
- isUdhaar: Boolean
- isPaid: Boolean
- soldAt: Long
```

#### 4. CustomerKhata
```kotlin
- id: Long (Primary Key, Auto-generate)
- customerName: String
- phoneNumber: String
- totalUdhaar: Double
- totalPaid: Double
- description: String
- createdAt: Long
- updatedAt: Long
```

#### 5. KhataTransaction
```kotlin
- id: Long (Primary Key, Auto-generate)
- customerId: Long (Foreign Key)
- type: TransactionType (UDHAAR_GIVEN / PAYMENT_RECEIVED)
- amount: Double
- description: String
- createdAt: Long
```

---

## 🧮 Profit Engine

### Automatic Profit Calculation
Every sale automatically calculates profit:
```
Profit = (Selling Price - Purchase Price) × Quantity
```

### Dashboard Stats (Real-time)
- **Total Profit:** Sum of all sale profits
- **Total Udhaar:** Sum of unpaid credit amounts
- **Cash Flow:** Total cash received from sales

---

## 🔐 Security Features

### EncryptedSharedPreferences
Stores sensitive data with AES-256 encryption:
- User PIN
- Shop Name
- Owner Name
- Setup completion status

### PIN Protection
- 4-digit PIN required on every app launch
- Secure PIN change in Settings
- PIN verification before changes

---

## 🎨 Android 15 Optimizations

### 1. Edge-to-Edge Display
```kotlin
enableEdgeToEdge()
```
- Content extends under system bars
- Proper padding for system UI

### 2. Material 3 (M3) Components
- Dynamic Color theming
- Modern card designs
- Animated transitions
- Rounded corners throughout

### 3. Predictive Back Gesture
```xml
android:enableOnBackInvokedCallback="true"
```
- Smooth back gesture animations
- Modern navigation experience

---

## 📂 Project Structure

```
app/src/main/java/com/mobileshop/erp/
├── MobileShopApp.kt              # Application class with Hilt
├── data/
│   ├── Converters.kt             # Room type converters
│   ├── MobileShopDatabase.kt     # Room database
│   ├── dao/
│   │   ├── CustomerKhataDao.kt
│   │   ├── ProductDao.kt
│   │   ├── SaleDao.kt
│   │   └── ShopProfileDao.kt
│   ├── entity/
│   │   ├── CustomerKhata.kt
│   │   ├── KhataTransaction.kt
│   │   ├── Product.kt
│   │   ├── Sale.kt
│   │   └── ShopProfile.kt
│   ├── repository/
│   │   ├── KhataRepository.kt
│   │   ├── ProductRepository.kt
│   │   └── SaleRepository.kt
│   └── security/
│       └── SecurePreferences.kt
├── di/
│   └── DatabaseModule.kt         # Hilt DI module
└── ui/
    ├── MainActivity.kt           # Main activity with edge-to-edge
    ├── navigation/
    │   ├── AppNavigation.kt
    │   └── Screen.kt
    ├── screens/
    │   ├── auth/
    │   │   ├── PinAuthScreen.kt
    │   │   └── PinAuthViewModel.kt
    │   ├── customer/
    │   │   ├── CustomerDetailScreen.kt
    │   │   └── CustomerDetailViewModel.kt
    │   ├── main/
    │   │   ├── MainScreen.kt
    │   │   ├── MainViewModel.kt
    │   │   └── pages/
    │   │       ├── InventoryPage.kt
    │   │       └── KhataPage.kt
    │   ├── product/
    │   │   ├── AddProductScreen.kt
    │   │   └── AddProductViewModel.kt
    │   ├── sale/
    │   │   ├── SellProductScreen.kt
    │   │   └── SellProductViewModel.kt
    │   ├── settings/
    │   │   ├── SettingsScreen.kt
    │   │   └── SettingsViewModel.kt
    │   └── setup/
    │       ├── SetupScreen.kt
    │       └── SetupViewModel.kt
    └── theme/
        ├── Color.kt
        ├── Theme.kt
        └── Type.kt
```

---

## 🔧 Key Features

| Feature | Description |
|---------|-------------|
| **First-Run Setup** | One-time shop details and PIN configuration |
| **PIN Authentication** | Secure access on every launch |
| **Swipe Dashboard** | Khata and Inventory pages |
| **Handset Tracking** | IMEI-based unique product tracking |
| **Accessory Tracking** | Quantity-based bulk inventory |
| **Customer Khata** | Credit/Udhaar management per customer |
| **Profit Calculation** | Automatic per-sale profit tracking |
| **Transaction History** | Complete Khata transaction log |
| **Search** | Find customers quickly |
| **Settings** | Edit shop details, change PIN |

---

## 📦 Dependencies

| Library | Version | Purpose |
|---------|---------|---------|
| Jetpack Compose BOM | 2024.12.01 | UI Framework |
| Room | 2.6.1 | Database |
| Hilt | 2.53.1 | Dependency Injection |
| Navigation Compose | 2.8.5 | Navigation |
| Security Crypto | 1.1.0-alpha06 | Encrypted Preferences |
| Coroutines | 1.9.0 | Async operations |

---

## 🏗️ Build & Run

### Prerequisites
- Android Studio Hedgehog or newer
- JDK 17
- Android SDK 35

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Clean and build
./gradlew clean assembleDebug
```

---

## 📱 Screens Overview

1. **Setup Screen** - First-time configuration
2. **PIN Auth Screen** - Security verification
3. **Main Screen** - Dashboard with swipe pages
4. **Add Product Screen** - Add handsets/accessories
5. **Sell Product Screen** - Record sales
6. **Customer Detail Screen** - View/manage customer Khata
7. **Settings Screen** - App configuration

---

## 🔄 Data Flow

```
User Input → ViewModel → Repository → DAO → Room Database
                ↓
           UI State (StateFlow)
                ↓
           Compose UI (Recomposition)
```

---

## ✅ Compliance

- ✅ Android 15 (API 35) target
- ✅ Edge-to-Edge display
- ✅ Material 3 Design
- ✅ Predictive Back Gesture
- ✅ Encrypted data storage
- ✅ MVVM Architecture
- ✅ Dependency Injection (Hilt)
- ✅ Kotlin Coroutines & Flow
- ✅ Room Database with migrations

---

**Developed:** January 2026  
**Version:** 1.0.0
