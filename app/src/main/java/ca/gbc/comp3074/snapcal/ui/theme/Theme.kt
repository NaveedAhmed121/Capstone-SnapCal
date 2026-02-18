package ca.gbc.comp3074.snapcal.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),
    onPrimary = Color(0xFF0B1F0D),
    primaryContainer = Color(0xFF1B5E20),
    onPrimaryContainer = Color(0xFFC8E6C9),

    secondary = Color(0xFF66BB6A),
    onSecondary = Color(0xFF0B1F0D),
    secondaryContainer = Color(0xFF2E7D32),
    onSecondaryContainer = Color(0xFFA5D6A7),

    tertiary = Color(0xFF4DB6AC),
    onTertiary = Color(0xFF001F1A),

    background = Color(0xFF0F1410),
    onBackground = Color(0xFFEAF3EA),
    surface = Color(0xFF121A13),
    onSurface = Color(0xFFEAF3EA),
    surfaceVariant = Color(0xFF1D2A1E),
    onSurfaceVariant = Color(0xFFCFE2D0),
    outline = Color(0xFF6F8A72),
)

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = GreenOnPrimary,
    primaryContainer = GreenPrimaryContainer,
    onPrimaryContainer = GreenOnPrimaryContainer,

    secondary = GreenSecondary,
    onSecondary = GreenOnSecondary,
    secondaryContainer = GreenSecondaryContainer,
    onSecondaryContainer = GreenOnSecondaryContainer,

    tertiary = GreenTertiary,
    onTertiary = GreenOnTertiary,
    tertiaryContainer = GreenTertiaryContainer,
    onTertiaryContainer = GreenOnTertiaryContainer,

    background = AppBackground,
    onBackground = Color(0xFF121212),
    surface = AppSurface,
    onSurface = Color(0xFF121212),
    surfaceVariant = AppSurfaceVariant,
    onSurfaceVariant = Color(0xFF2A3A2B),
    outline = AppOutline
)

@Composable
fun SnapCalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // ✅ IMPORTANT: keep your green theme, don’t override with device colors
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
