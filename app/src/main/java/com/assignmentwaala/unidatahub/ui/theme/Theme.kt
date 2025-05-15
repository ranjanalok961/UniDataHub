package com.assignmentwaala.unidatahub.ui.theme

import android.app.Activity
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
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun UniDataHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DeepOceanTheme.darkColorScheme
        else -> DeepOceanTheme.lightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}



object DeepOceanTheme {
    // Light Theme
    val lightPrimary = Color(0xFF1A73E8)
    val lightOnPrimary = Color(0xFFFFFFFF)
    val lightPrimaryContainer = Color(0xFFD8E6FF)
    val lightOnPrimaryContainer = Color(0xFF001C38)
    val lightSecondary = Color(0xFF00BCD4)
    val lightOnSecondary = Color(0xFFFFFFFF)
    val lightSecondaryContainer = Color(0xFFB3EBFF)
    val lightOnSecondaryContainer = Color(0xFF001F24)
    val lightTertiary = Color(0xFF6200EE)
    val lightOnTertiary = Color(0xFFFFFFFF)
    val lightBackground = Color(0xFFF8F9FA)
    val lightOnBackground = Color(0xFF202124)
    val lightSurface = Color(0xFFFFFFFF)
    val lightOnSurface = Color(0xFF202124)
    val lightError = Color(0xFFB00020)
    val lightOnError = Color(0xFFFFFFFF)

    // Dark Theme
    val darkPrimary = Color(0xFF90CAF9)
    val darkOnPrimary = Color(0xFF00295C)
    val darkPrimaryContainer = Color(0xFF084B8A)
    val darkOnPrimaryContainer = Color(0xFFD8E6FF)
    val darkSecondary = Color(0xFF80DEEA)
    val darkOnSecondary = Color(0xFF00363D)
    val darkSecondaryContainer = Color(0xFF004F58)
    val darkOnSecondaryContainer = Color(0xFFB3EBFF)
    val darkTertiary = Color(0xFFBB86FC)
    val darkOnTertiary = Color(0xFF21005E)
    val darkBackground = Color(0xFF121212)
    val darkOnBackground = Color(0xFFE3E3E3)
    val darkSurface = Color(0xFF202124)
    val darkOnSurface = Color(0xFFE3E3E3)
    val darkError = Color(0xFFCF6679)
    val darkOnError = Color(0xFF330007)

    // Gradient Colors
    val gradients = mapOf(
        "blueGradient" to Pair(Color(0xFF1A73E8), Color(0xFF6AB7FF)),
        "purpleGradient" to Pair(Color(0xFF6200EE), Color(0xFFBB86FC)),
        "cyanGradient" to Pair(Color(0xFF00BCD4), Color(0xFF80DEEA))
    )

    // Color Schemes
    val lightColorScheme = lightColorScheme(
        primary = lightPrimary,
        onPrimary = lightOnPrimary,
        primaryContainer = lightPrimaryContainer,
        onPrimaryContainer = lightOnPrimaryContainer,
        secondary = lightSecondary,
        onSecondary = lightOnSecondary,
        secondaryContainer = lightSecondaryContainer,
        onSecondaryContainer = lightOnSecondaryContainer,
        tertiary = lightTertiary,
        onTertiary = lightOnTertiary,
        background = lightBackground,
        onBackground = lightOnBackground,
        surface = lightSurface,
        onSurface = lightOnSurface,
        error = lightError,
        onError = lightOnError
    )

    val darkColorScheme = darkColorScheme(
        primary = darkPrimary,
        onPrimary = darkOnPrimary,
        primaryContainer = darkPrimaryContainer,
        onPrimaryContainer = darkOnPrimaryContainer,
        secondary = darkSecondary,
        onSecondary = darkOnSecondary,
        secondaryContainer = darkSecondaryContainer,
        onSecondaryContainer = darkOnSecondaryContainer,
        tertiary = darkTertiary,
        onTertiary = darkOnTertiary,
        background = darkBackground,
        onBackground = darkOnBackground,
        surface = darkSurface,
        onSurface = darkOnSurface,
        error = darkError,
        onError = darkOnError
    )
}