package com.mobileshop.erp.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.mobileshop.erp.data.security.SecurePreferences
import com.mobileshop.erp.ui.navigation.AppNavigation
import com.mobileshop.erp.ui.navigation.Screen
import com.mobileshop.erp.ui.theme.MobileShopERPTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var securePreferences: SecurePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Enable edge-to-edge display for Android 15
        enableEdgeToEdge()

        setContent {
            MobileShopERPTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                        .imePadding()
                ) {
                    MobileShopApp(securePreferences)
                }
            }
        }
    }
}

@Composable
private fun MobileShopApp(securePreferences: SecurePreferences) {
    val navController = rememberNavController()
    
    // Determine start destination based on setup status
    val startDestination by remember {
        mutableStateOf(
            when {
                !securePreferences.isSetupCompleted() -> Screen.Setup.route
                else -> Screen.PinAuth.route
            }
        )
    }

    AppNavigation(
        navController = navController,
        startDestination = startDestination
    )
}
