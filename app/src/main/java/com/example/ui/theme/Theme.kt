package com.example.ui.theme

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

private val DarkColorScheme =
  darkColorScheme(
    primary = SolidBronze,
    onPrimary = SoftWhite,
    secondary = SolidBronze,
    onSecondary = SoftWhite,
    tertiary = WarmGoldAccent,
    onTertiary = CleanTextPrimary,
    background = CleanTextPrimary,
    surface = CleanTextSecondary,
    onBackground = CleanBackground,
    onSurface = CleanBackground,
    onSurfaceVariant = CleanTextMuted,
    error = RedHighlight
  )

private val LightColorScheme =
  lightColorScheme(
    primary = SolidBronze,
    onPrimary = SoftWhite,
    secondary = SandOffWhite,
    onSecondary = CleanTextPrimary,
    tertiary = WarmGoldAccent,
    onTertiary = CleanTextPrimary,
    background = CleanBackground,
    surface = SoftWhite,
    onBackground = CleanTextPrimary,
    onSurface = CleanTextPrimary,
    onSurfaceVariant = CleanTextSecondary,
    error = RedHighlight
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Boot into Clean Minimalism's graceful sand-white style by default
  dynamicColor: Boolean = false, // Consistent artisanal brand aesthetic
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
