package ca.gbc.comp3074.snapcal.ui

import androidx.compose.runtime.Composable
import ca.gbc.comp3074.snapcal.ui.navigation.SnapCalApp
import ca.gbc.comp3074.snapcal.ui.theme.SnapCalTheme

@Composable
fun SnapCallAppRoot() {
    // ✅ dynamicColor is off by default now (Theme.kt)
    SnapCalTheme {
        SnapCalApp()
    }
}