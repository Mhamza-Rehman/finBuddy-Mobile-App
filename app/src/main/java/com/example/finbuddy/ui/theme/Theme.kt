package com.example.finbuddy.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF2E7D32),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    secondaryContainer = Color(0xFF2D2A2E),
    onBackground = Color(0xFFE3E2E6),
    onSurface = Color(0xFFE3E2E6)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0F9D58),
    secondary = Color(0xFFFFFFFF),
    tertiary = Color(0xFF0F9D58),
    background = Color(0xFFF8F9FA),
    surface = Color(0xFFFFFFFF)
)

@Composable
fun FinBuddyTheme(
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
        typography = AppTypography,
        content = content
    )
}
