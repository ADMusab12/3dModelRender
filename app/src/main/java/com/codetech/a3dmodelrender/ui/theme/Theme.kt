package com.codetech.a3dmodelrender.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Define white-based colors
private val White = Color(0xFFFFFFFF)
private val OffWhite = Color(0xFFF8F8F8)
private val LightGray = Color(0xFFEEEEEE)
private val VeryLightPurple = Color(0xFFF0E6FF)
private val VeryLightPink = Color(0xFFFFF0F5)

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = White,
    secondary = OffWhite,
    tertiary = White,

    // Override with very light colors
    background = White,
    surface = OffWhite,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = LightGray,
    surfaceTint = VeryLightPurple,
    secondaryContainer = VeryLightPink
)


@Composable
fun _3dModelRenderTheme(
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            dynamicLightColorScheme(context)
        }
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}