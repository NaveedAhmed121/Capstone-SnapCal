package ca.gbc.comp3074.snapcal.ui.navigation

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ca.gbc.comp3074.snapcal.data.db.DatabaseProvider
import ca.gbc.comp3074.snapcal.data.repo.MealRepository
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository
import ca.gbc.comp3074.snapcal.ui.auth.AuthState
import ca.gbc.comp3074.snapcal.ui.components.SnapCalBottomBar
import ca.gbc.comp3074.snapcal.ui.healthconnect.HealthConnectViewModel
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel
import ca.gbc.comp3074.snapcal.ui.screens.DashboardScreen
import ca.gbc.comp3074.snapcal.ui.screens.LoginScreen
import ca.gbc.comp3074.snapcal.ui.screens.ManualMealScreen
import ca.gbc.comp3074.snapcal.ui.screens.MenuScreen
import ca.gbc.comp3074.snapcal.ui.screens.PlannerScreen
import ca.gbc.comp3074.snapcal.ui.screens.ProgressScreen
import ca.gbc.comp3074.snapcal.ui.screens.ScanScreen
import ca.gbc.comp3074.snapcal.ui.screens.ScanViewModel
import ca.gbc.comp3074.snapcal.ui.screens.ShoppingScreen
import ca.gbc.comp3074.snapcal.ui.screens.WaterScreen
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModelFactory
import ca.gbc.comp3074.snapcal.viewmodel.MealsViewModel
import ca.gbc.comp3074.snapcal.viewmodel.MealsViewModelFactory
import ca.gbc.comp3074.snapcal.viewmodel.ProgressViewModel
import ca.gbc.comp3074.snapcal.viewmodel.ProgressViewModelFactory

@Composable
fun SnapCalApp() {
    MaterialTheme {
        val navController = rememberNavController()
        val context = LocalContext.current

        val backStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = backStackEntry?.destination?.route ?: Routes.DASHBOARD

        val startDestination =
            if (AuthState.isLoggedIn.value) Routes.DASHBOARD else Routes.LOGIN

        // DB + Repos
        val db = remember { DatabaseProvider.get(context) }
        val mealRepo = remember { MealRepository(db.mealLogDao()) }
        val waterRepo = remember { WaterRepository(db.waterDao()) }

        // ViewModels
        val mealsVm: MealsViewModel = viewModel(factory = MealsViewModelFactory(mealRepo))
        val progressVm: ProgressViewModel = viewModel(factory = ProgressViewModelFactory(mealRepo, waterRepo))
        val waterVm: WaterViewModel = viewModel(factory = WaterViewModelFactory(waterRepo))
        val scanVm: ScanViewModel = viewModel()

        // HealthConnectViewModel is AndroidViewModel (needs Application)
        val app = context.applicationContext as Application
        val healthConnectVm: HealthConnectViewModel =
            viewModel(factory = ViewModelProvider.AndroidViewModelFactory(app))

        val mealPlanVm: MealPlanViewModel = viewModel()

        // Hide bottom bar on these routes
        val showBottomBar = currentRoute !in listOf(
            Routes.LOGIN,
            Routes.MANUAL_MEAL,
            Routes.SCAN
        )

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    SnapCalBottomBar(currentRoute = currentRoute) { route ->
                        if (route != currentRoute) {
                            navController.navigate(route) {
                                launchSingleTop = true
                                restoreState = true
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = startDestination,
                modifier = Modifier.padding(paddingValues)
            ) {

                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess = {
                            AuthState.isLoggedIn.value = true
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Routes.DASHBOARD) {
                    DashboardScreen(
                        mealsVm = mealsVm,
                        waterVm = waterVm,
                        healthConnectVm = healthConnectVm,
                        mealPlanVm = mealPlanVm,
                        onAddManual = { navController.navigate(Routes.MANUAL_MEAL) },
                        onScan = { navController.navigate(Routes.SCAN) },
                        onProgress = { navController.navigate(Routes.PROGRESS) },
                        onMenu = { navController.navigate(Routes.MENU) },
                        onPlanner = { navController.navigate(Routes.PLANNER) }
                    )
                }

                // ✅ ADD THIS ROUTE (even if not in bottom bar)
                composable(Routes.WATER) {
                    WaterScreen(vm = waterVm)
                }

                composable(Routes.MENU) {
                    MenuScreen(
                        mealPlanVm = mealPlanVm,
                        onGoPlanner = { navController.navigate(Routes.PLANNER) }
                    )
                }

                composable(Routes.PLANNER) {
                    PlannerScreen(
                        mealPlanVm = mealPlanVm,
                        onGoShopping = { navController.navigate(Routes.SHOPPING) },
                        onGoMenu = { navController.navigate(Routes.MENU) }
                    )
                }

                composable(Routes.SHOPPING) {
                    ShoppingScreen(
                        mealPlanVm = mealPlanVm,
                        onGoPlanner = { navController.navigate(Routes.PLANNER) }
                    )
                }

                composable(Routes.MANUAL_MEAL) {
                    ManualMealScreen(
                        onBack = { navController.popBackStack() },
                        onSave = { name, cals, p, c, f ->
                            mealsVm.addMeal(name, cals, p, c, f)
                            navController.popBackStack()
                        }
                    )
                }

                composable(Routes.SCAN) {
                    ScanScreen(
                        onBack = { navController.popBackStack() },
                        onBarcodeScanned = { barcode ->
                            val product = scanVm.product
                            if (product != null) {
                                mealsVm.addMeal(
                                    name = product.product_name ?: "Scanned Product",
                                    calories = product.nutriments?.energy_kcal_100g?.toInt() ?: 0,
                                    protein = product.nutriments?.proteins_100g?.toInt() ?: 0,
                                    carbs = product.nutriments?.carbohydrates_100g?.toInt() ?: 0,
                                    fat = product.nutriments?.fat_100g?.toInt() ?: 0
                                )
                            } else {
                                mealsVm.addMeal(
                                    name = "Scanned: $barcode",
                                    calories = 0,
                                    protein = 0,
                                    carbs = 0,
                                    fat = 0
                                )
                            }
                            navController.popBackStack()
                        },
                        scanViewModel = scanVm
                    )
                }

                composable(Routes.PROGRESS) {
                    ProgressScreen(
                        onBack = { navController.popBackStack() },
                        progressVm = progressVm,
                        healthConnectVm = healthConnectVm
                    )
                }
            }
        }
    }
}
