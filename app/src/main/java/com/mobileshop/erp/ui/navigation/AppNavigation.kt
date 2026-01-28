package com.mobileshop.erp.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.mobileshop.erp.ui.screens.main.MainScreen
import com.mobileshop.erp.ui.screens.auth.PinAuthScreen
import com.mobileshop.erp.ui.screens.setup.SetupScreen
import com.mobileshop.erp.ui.screens.settings.SettingsScreen
import com.mobileshop.erp.ui.screens.product.AddProductScreen
import com.mobileshop.erp.ui.screens.customer.CustomerDetailScreen
import com.mobileshop.erp.ui.screens.sale.SellProductScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { 300 },
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { -300 },
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                initialOffsetX = { -300 },
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                targetOffsetX = { 300 },
                animationSpec = tween(300)
            )
        }
    ) {
        composable(Screen.Setup.route) {
            SetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Setup.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.PinAuth.route) {
            PinAuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.PinAuth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToCustomerDetail = { customerId ->
                    navController.navigate(Screen.CustomerDetail.createRoute(customerId))
                },
                onNavigateToAddProduct = { productType ->
                    navController.navigate(Screen.AddProduct.createRoute(productType))
                },
                onNavigateToSellProduct = { productId ->
                    navController.navigate(Screen.SellProduct.createRoute(productId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(navArgument("customerId") { type = NavType.LongType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
            CustomerDetailScreen(
                customerId = customerId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.AddProduct.route,
            arguments = listOf(navArgument("productType") { type = NavType.StringType })
        ) { backStackEntry ->
            val productType = backStackEntry.arguments?.getString("productType") ?: "HANDSET"
            AddProductScreen(
                productType = productType,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.SellProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.LongType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getLong("productId") ?: 0L
            SellProductScreen(
                productId = productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
