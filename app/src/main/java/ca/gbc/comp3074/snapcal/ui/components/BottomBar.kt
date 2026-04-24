package ca.gbc.comp3074.snapcal.ui.components
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import ca.gbc.comp3074.snapcal.ui.navigation.Routes

data class BottomNavItem(val route:String, val label:String, val icon:ImageVector, val emoji:String)
private val items = listOf(
    BottomNavItem(Routes.DASHBOARD,"Home",Icons.Filled.Home,"🍓"),
    BottomNavItem(Routes.MENU,"Menu",Icons.Filled.MenuBook,"🍽️"),
    BottomNavItem(Routes.PLANNER,"Plan",Icons.Filled.Today,"📅"),
    BottomNavItem(Routes.SHOPPING,"Shopping",Icons.Filled.ShoppingCart,"🛒"),
    BottomNavItem(Routes.PROGRESS,"Progress",Icons.Filled.ShowChart,"🥕")
)
@Composable
fun SnapCalBottomBar(currentRoute: String?, onNavigate: (String)->Unit) {
    val route = currentRoute ?: Routes.DASHBOARD
    NavigationBar {
        items.forEach { item ->
            NavigationBarItem(
                selected = route==item.route || route.startsWith(item.route),
                onClick = { onNavigate(item.route) },
                icon = { Icon(item.icon, item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
