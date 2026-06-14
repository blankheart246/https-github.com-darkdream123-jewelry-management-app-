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

enum class UserTheme {
    LIGHT, BLACK, BLUE, GREEN, RED, PURPLE
}

private val BlackColorScheme = darkColorScheme(
    primary = Color.White,
    onPrimary = PureBlack,
    background = PureBlack,
    surface = DarkGrey,
    onBackground = Color.White,
    onSurface = Color.White,
    secondary = Color.Gray,
    onSecondary = Color.White
)

private val BlueColorScheme = darkColorScheme(
    primary = OnSolidBlue,
    onPrimary = SolidBlue,
    background = SolidBlue,
    surface = Color(0xFF003060),
    onBackground = OnSolidBlue,
    onSurface = OnSolidBlue,
    secondary = OnSolidBlue,
    onSecondary = SolidBlue
)

private val GreenColorScheme = darkColorScheme(
    primary = OnSolidGreen,
    onPrimary = SolidGreen,
    background = SolidGreen,
    surface = Color(0xFF003300),
    onBackground = OnSolidGreen,
    onSurface = OnSolidGreen,
    secondary = OnSolidGreen,
    onSecondary = SolidGreen
)

private val RedColorScheme = darkColorScheme(
    primary = OnSolidRed,
    onPrimary = SolidRed,
    background = SolidRed,
    surface = Color(0xFF500000),
    onBackground = OnSolidRed,
    onSurface = OnSolidRed,
    secondary = OnSolidRed,
    onSecondary = SolidRed
)

private val PurpleColorScheme = darkColorScheme(
    primary = OnSolidPurple,
    onPrimary = SolidPurple,
    background = SolidPurple,
    surface = Color(0xFF311060),
    onBackground = OnSolidPurple,
    onSurface = OnSolidPurple,
    secondary = OnSolidPurple,
    onSecondary = SolidPurple
)

@Composable
fun MyApplicationTheme(
    theme: UserTheme = UserTheme.LIGHT,
    content: @Composable () -> Unit,
) {
    val colorScheme = when (theme) {
        UserTheme.LIGHT -> LightColorScheme
        UserTheme.BLACK -> BlackColorScheme
        UserTheme.BLUE -> BlueColorScheme
        UserTheme.GREEN -> GreenColorScheme
        UserTheme.RED -> RedColorScheme
        UserTheme.PURPLE -> PurpleColorScheme
    }
    MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
