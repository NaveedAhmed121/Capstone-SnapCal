package ca.gbc.comp3074.snapcal.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary=PinkPrimary, onPrimary=Color.White,
    primaryContainer=Color(0xFFFFD2DE), onPrimaryContainer=PinkDark,
    secondary=PeachAccent, onSecondary=Color.White,
    secondaryContainer=Color(0xFFFFEEC4),
    background=SurfaceWarm, onBackground=Color(0xFF1A1A1A),
    surface=CardWhite, onSurface=Color(0xFF1A1A1A),
    surfaceVariant=Color(0xFFF0F4FF), onSurfaceVariant=SubtleGray,
    outline=Color(0xFFE5E5E5), error=Color(0xFFBA1A1A)
)
private val DarkColors = darkColorScheme(
    primary=PinkLight, onPrimary=NavyDark,
    primaryContainer=Color(0xFF6B2040), onPrimaryContainer=Color(0xFFFFD2DE),
    secondary=PeachAccent, onSecondary=NavyDark,
    background=NavyDark, onBackground=Color.White,
    surface=SlateDark, onSurface=Color.White,
    surfaceVariant=Color(0xFF222B3E), onSurfaceVariant=Color(0xFFB0B8CC)
)

@Composable
fun SnapCalTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme=if(darkTheme) DarkColors else LightColors, typography=Typography, content=content)
}
