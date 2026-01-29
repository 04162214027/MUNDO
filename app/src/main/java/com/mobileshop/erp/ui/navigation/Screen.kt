package com.mobileshop.erp.ui.navigation

sealed class Screen(val route: String) {
    data object Setup : Screen("setup")
    data object PinAuth : Screen("pin_auth")
    data object Login : Screen("login")
    data object Main : Screen("main")
    data object Settings : Screen("settings")
    data object TransactionHistory : Screen("transaction_history")
    data object PurchaseOldPhone : Screen("purchase_old_phone")
    data object CustomerDetail : Screen("customer_detail/{customerId}") {
        fun createRoute(customerId: Long) = "customer_detail/$customerId"
    }
    data object AddProduct : Screen("add_product/{productType}") {
        fun createRoute(productType: String) = "add_product/$productType"
    }
    data object SellProduct : Screen("sell_product/{productId}") {
        fun createRoute(productId: Long) = "sell_product/$productId"
    }
}
