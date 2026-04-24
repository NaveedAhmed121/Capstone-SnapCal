package ca.gbc.comp3074.snapcal.ui.navigation

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import ca.gbc.comp3074.snapcal.data.db.DatabaseProvider
import ca.gbc.comp3074.snapcal.data.repo.*
import ca.gbc.comp3074.snapcal.ui.auth.AuthState
import ca.gbc.comp3074.snapcal.ui.components.SnapCalBottomBar
import ca.gbc.comp3074.snapcal.ui.healthconnect.HealthConnectViewModel
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel
import ca.gbc.comp3074.snapcal.ui.screens.*
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModelFactory
import ca.gbc.comp3074.snapcal.ui.workout.WorkoutViewModel
import ca.gbc.comp3074.snapcal.ui.workout.WorkoutViewModelFactory
import ca.gbc.comp3074.snapcal.viewmodel.*

@Composable
fun SnapCalApp() {
    val navController = rememberNavController()
    val context       = LocalContext.current
    val backStack    by navController.currentBackStackEntryAsState()
    val currentRoute  = backStack?.destination?.route ?: Routes.DASHBOARD

    // Bottom-bar tab navigation (keeps back-stack clean)
    val navigateTo: (String) -> Unit = { route ->
        if (route != currentRoute) {
            navController.navigate(route) {
                launchSingleTop = true
                restoreState    = true
                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            }
        }
    }

    // Repositories – created once per composition lifetime
    val db          = remember { DatabaseProvider.getDatabase(context) }
    val mealRepo    = remember { MealRepository(db.mealLogDao()) }
    val waterRepo   = remember { WaterRepository(db.waterDao()) }
    val workoutRepo = remember { WorkoutRepository(db.workoutDao()) }
    val userRepo    = remember { UserRepository(db.userDao()) }

    // ViewModels
    val mealsVm:    MealsViewModel    = viewModel(factory = MealsViewModelFactory(mealRepo))
    val progressVm: ProgressViewModel = viewModel(factory = ProgressViewModelFactory(mealRepo, waterRepo, workoutRepo))
    val waterVm:    WaterViewModel    = viewModel(factory = WaterViewModelFactory(waterRepo))
    val workoutVm:  WorkoutViewModel  = viewModel(factory = WorkoutViewModelFactory(workoutRepo))
    val scanVm:     ScanViewModel     = viewModel()
    val searchVm:   RecipeSearchViewModel = viewModel()
    val mealPlanVm: MealPlanViewModel = viewModel()
    val authVm:     AuthViewModel     = viewModel(factory = AuthViewModelFactory(userRepo))
    
    val app = context.applicationContext as Application
    val healthVm: HealthConnectViewModel =
        viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(app))

    val startDest   = if (AuthState.isLoggedIn.value) Routes.DASHBOARD else Routes.LOGIN
    // Routes where the bottom bar should be hidden
    val noBottomBar = setOf(
        Routes.LOGIN, Routes.MANUAL_MEAL, Routes.SCAN,
        Routes.RECIPE_SEARCH, Routes.WORKOUT, Routes.WATER,
        Routes.PROGRESS, Routes.SETTINGS, Routes.ADMIN
    )
    val showBottom = currentRoute !in noBottomBar

    Scaffold(
        bottomBar = { if (showBottom) SnapCalBottomBar(currentRoute, navigateTo) }
    ) { pad ->
        NavHost(
            navController    = navController,
            startDestination = startDest,
            modifier         = Modifier.padding(pad)
        ) {

            // LOGIN
            composable(Routes.LOGIN) {
                LoginScreen(
                    authVm = authVm,
                    onLoginSuccess = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onAdminClick = { navController.navigate(Routes.ADMIN) }
                )
            }

            // DASHBOARD
            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    mealsVm         = mealsVm,
                    waterVm         = waterVm,
                    healthConnectVm = healthVm,
                    mealPlanVm      = mealPlanVm,
                    workoutVm       = workoutVm,
                    onAddManual     = { navController.navigate(Routes.MANUAL_MEAL) },
                    onScan          = { navController.navigate(Routes.SCAN) },
                    onRecipeSearch  = { navController.navigate(Routes.RECIPE_SEARCH) },
                    onProgress      = { navController.navigate(Routes.PROGRESS) },
                    onMenu          = { navigateTo(Routes.MENU) },
                    onPlanner       = { navigateTo(Routes.PLANNER) },
                    onWorkout       = { navController.navigate(Routes.WORKOUT) },
                    onWater         = { navController.navigate(Routes.WATER) },
                    onSettings      = { navController.navigate(Routes.SETTINGS) }
                )
            }

            // WATER – full screen with back
            composable(Routes.WATER) {
                WaterScreen(vm = waterVm, onBack = { navController.popBackStack() })
            }

            // MENU
            composable(Routes.MENU) {
                MenuScreen(
                    mealPlanVm = mealPlanVm,
                    onBack = { navController.popBackStack() },
                    onGoPlanner = { navigateTo(Routes.PLANNER) },
                    onBrowseRecipes = { navController.navigate(Routes.RECIPE_SEARCH) }
                )
            }

            // PLANNER
            composable(Routes.PLANNER) {
                PlannerScreen(
                    mealPlanVm   = mealPlanVm,
                    onBack       = { navController.popBackStack() },
                    onGoShopping = { navigateTo(Routes.SHOPPING) },
                    onGoMenu     = { navigateTo(Routes.MENU) }
                )
            }

            // SHOPPING
            composable(Routes.SHOPPING) {
                ShoppingScreen(
                    mealPlanVm = mealPlanVm,
                    onBack = { navController.popBackStack() },
                    onGoPlanner = { navigateTo(Routes.PLANNER) }
                )
            }

            // WORKOUT – full screen with back
            composable(Routes.WORKOUT) {
                WorkoutScreen(vm = workoutVm, onBack = { navController.popBackStack() })
            }

            // MANUAL MEAL
            composable(Routes.MANUAL_MEAL) {
                ManualMealScreen(
                    onBack = { navController.popBackStack() },
                    onSave = { name, cals, p, c, f, mealType, cuisine, goalType ->
                        mealsVm.addMeal(name, cals, p, c, f, mealType, cuisine, goalType)
                        navController.popBackStack()
                    }
                )
            }

            // SCAN
            composable(Routes.SCAN) {
                ScanScreen(
                    onBack           = { navController.popBackStack() },
                    onBarcodeScanned = { barcode ->
                        val product = scanVm.product
                        mealsVm.addMeal(
                            name     = product?.product_name?.takeIf { it.isNotBlank() } ?: "Scanned: $barcode",
                            calories = product?.nutriments?.energy_kcal_100g?.toInt() ?: 0,
                            protein  = product?.nutriments?.proteins_100g?.toInt() ?: 0,
                            carbs    = product?.nutriments?.carbohydrates_100g?.toInt() ?: 0,
                            fat      = product?.nutriments?.fat_100g?.toInt() ?: 0,
                            mealType = "Snack",
                            cuisine  = "Scanned",
                            goalType = mealPlanVm.uiState.value.selectedGoal.name
                        )
                        navController.popBackStack()
                    },
                    scanViewModel = scanVm
                )
            }

            // RECIPE SEARCH – Quick Import
            composable(Routes.RECIPE_SEARCH) {
                RecipeSearchScreen(
                    mealsVm  = mealsVm,
                    onBack   = { navController.popBackStack() },
                    searchVm = searchVm
                )
            }

            // PROGRESS – full screen with back
            composable(Routes.PROGRESS) {
                ProgressScreen(
                    onBack          = { navController.popBackStack() },
                    progressVm      = progressVm,
                    healthConnectVm = healthVm
                )
            }

            // ADMIN
            composable(Routes.ADMIN) {
                AdminScreen(
                    userRepo = userRepo,
                    onBack = { navController.popBackStack() }
                )
            }

            // SETTINGS – full screen with back + logout
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack   = { navController.popBackStack() },
                    onLogout = {
                        AuthState.isLoggedIn.value = false
                        AuthState.currentUser.value = null
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
