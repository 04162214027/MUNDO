package com.mobileshop.erp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Blue40,
    primaryContainer = Blue40,
    onPrimaryContainer = Blue80,
    secondary = Teal80,
    onSecondary = Teal40,
    secondaryContainer = Teal40,
    onSecondaryContainer = Teal80,
    tertiary = Orange80,
    onTertiary = Orange40,
    tertiaryContainer = Orange40,
    onTertiaryContainer = Orange80,
    background = BackgroundDark,
    onBackground = SurfaceLight,
    surface = SurfaceDark,
    onSurface = SurfaceLight
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = SurfaceLight,
    primaryContainer = Blue80,
    onPrimaryContainer = Blue40,
    secondary = Teal40,
    onSecondary = SurfaceLight,
    secondaryContainer = Teal80,
    onSecondaryContainer = Teal40,
    tertiary = Orange40,
    onTertiary = SurfaceLight,
    tertiaryContainer = Orange80,
    onTertiaryContainer = Orange40,
    background = BackgroundLight,
    onBackground = SurfaceDark,
    surface = SurfaceLight,
    onSurface = SurfaceDark
)

@Composable
fun MobileShopERPTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
