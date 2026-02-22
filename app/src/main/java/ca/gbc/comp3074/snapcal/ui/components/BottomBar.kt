package ca.gbc.comp3074.snapcal.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ca.gbc.comp3074.snapcal.ui.navigation.Routes

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val items = listOf(
    BottomNavItem(Routes.DASHBOARD, "Dashboard", Icons.Filled.Home),
    BottomNavItem(Routes.MENU, "Menu", Icons.Filled.List),
    BottomNavItem(Routes.PLANNER, "Plan", Icons.Filled.Today),
    BottomNavItem(Routes.SHOPPING, "Shopping", Icons.Filled.ShoppingCart),
    BottomNavItem(Routes.PROGRESS, "Progress", Icons.Filled.ShowChart),
)

@Composable
fun SnapCalBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val routeNow = currentRoute ?: Routes.DASHBOARD

    NavigationBar {
        items.forEach { item ->
            // Safer: routeNow may include args (e.g., "menu?id=1")
            val selected = routeNow == item.route || routeNow.startsWith(item.route)

            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.route) },
                icon = { Icon(imageVector = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}
