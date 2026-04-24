package ca.gbc.comp3074.snapcal.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import ca.gbc.comp3074.snapcal.ui.healthconnect.HealthConnectViewModel
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel
import ca.gbc.comp3074.snapcal.ui.theme.*
import ca.gbc.comp3074.snapcal.ui.water.WaterViewModel
import ca.gbc.comp3074.snapcal.ui.workout.WorkoutViewModel
import ca.gbc.comp3074.snapcal.viewmodel.MealsViewModel
import java.util.Calendar

@Composable
fun DashboardScreen(
    mealsVm: MealsViewModel, waterVm: WaterViewModel,
    healthConnectVm: HealthConnectViewModel, mealPlanVm: MealPlanViewModel, workoutVm: WorkoutViewModel,
    onAddManual:()->Unit, onScan:()->Unit, onRecipeSearch:()->Unit,
    onProgress:()->Unit, onMenu:()->Unit, onPlanner:()->Unit,
    onWorkout:()->Unit, onWater:()->Unit,
    onSettings:()->Unit
) {
    val context = LocalContext.current
    val mealState by mealsVm.uiState.collectAsState()
    val todayWater by waterVm.todayTotalMl.collectAsState(initial=0)
    val plannerState by mealPlanVm.uiState.collectAsState()
    val steps by healthConnectVm.steps
    val hasHCPerms by healthConnectVm.hasHealthConnectPermissions
    val hasNormalPerms by healthConnectVm.hasNormalPermissions
    val todayBurned    by workoutVm.todayBurned.collectAsState(initial=0)
    val allWorkouts    by workoutVm.allEntries.collectAsState(initial=emptyList())
    
    // Calculate start of today in millis to get truly "upcoming" workouts
    val todayStartMillis = remember {
        Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }
    val upcomingWk     by workoutVm.observeUpcoming(todayStartMillis).collectAsState(initial=emptyList())
    val balance = mealState.todayCalories - todayBurned

    val hcLauncher = rememberLauncherForActivityResult(healthConnectVm.permissionRequestContract()) {
        healthConnectVm.refreshHealthConnectPermissionsState(context); healthConnectVm.refreshSteps(context) }
    val normalLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        healthConnectVm.refreshNormalPermissionsState(context)
        // After normal perms granted, check HC perms and load steps
        healthConnectVm.refreshHealthConnectPermissionsState(context)
        healthConnectVm.refreshSteps(context) }
    LaunchedEffect(Unit) { healthConnectVm.initialCheck(context) }

    LazyColumn(Modifier.fillMaxSize(), contentPadding=PaddingValues(bottom=16.dp)) {
        // Pink gradient header
        item {
            Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(PinkLight,PeachAccent))).padding(20.dp)) {
                Column(verticalArrangement=Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement=Arrangement.SpaceBetween, modifier=Modifier.fillMaxWidth(), verticalAlignment=Alignment.CenterVertically) {
                        Column { Text("Dashboard", style=MaterialTheme.typography.headlineMedium, fontWeight=FontWeight.Bold, color=Color.White); Text("SnapCal", style=MaterialTheme.typography.bodyMedium, color=Color.White.copy(0.8f)) }
                        IconButton(onClick=onSettings) { Icon(Icons.Default.Settings, "Settings", tint=Color.White, modifier=Modifier.size(28.dp)) }
                    }
                    // Calorie ring card
                    Card(modifier=Modifier.fillMaxWidth(), shape=RoundedCornerShape(16.dp), colors=CardDefaults.cardColors(containerColor=Color.White.copy(0.92f))) {
                        Row(Modifier.padding(16.dp), horizontalArrangement=Arrangement.spacedBy(16.dp), verticalAlignment=Alignment.CenterVertically) {
                            // Progress circle approximation
                            Box(Modifier.size(88.dp).clip(CircleShape).background(Color(0xFFE5E5E5)), contentAlignment=Alignment.Center) {
                                Box(Modifier.size(68.dp).clip(CircleShape).background(Color.White), contentAlignment=Alignment.Center) {
                                    Text("⚖️", fontSize=24.sp)
                                }
                            }
                            Column(verticalArrangement=Arrangement.spacedBy(6.dp)) {
                                Text("Calories (in vs out)", style=MaterialTheme.typography.titleSmall, color=PinkDark, fontWeight=FontWeight.SemiBold)
                                Text("In: ${mealState.todayCalories} kcal   Out: $todayBurned kcal", style=MaterialTheme.typography.bodyMedium)
                                Text("Balance: ${if(balance>=0)"+$balance" else "$balance"} kcal", fontWeight=FontWeight.Bold, color=if(balance>=0) Color(0xFF28A745) else Color(0xFFDC3545))
                                Row(horizontalArrangement=Arrangement.spacedBy(16.dp)) {
                                    Row(horizontalArrangement=Arrangement.spacedBy(4.dp), verticalAlignment=Alignment.CenterVertically) { Box(Modifier.size(12.dp).background(BlueAccent,RoundedCornerShape(2.dp))); Text("Burned",style=MaterialTheme.typography.bodySmall) }
                                    Row(horizontalArrangement=Arrangement.spacedBy(4.dp), verticalAlignment=Alignment.CenterVertically) { Box(Modifier.size(12.dp).background(Color(0xFFE5E5E5),RoundedCornerShape(2.dp))); Text("Consumed",style=MaterialTheme.typography.bodySmall) }
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Column(Modifier.padding(horizontal=16.dp).padding(top=12.dp), verticalArrangement=Arrangement.spacedBy(12.dp)) {
                // Weekly goal card
                Card(Modifier.fillMaxWidth(), shape=RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                        Text("🎯 Goal & Plan", style=MaterialTheme.typography.titleSmall, color=PinkDark, fontWeight=FontWeight.SemiBold)
                        Text("Goal: ${plannerState.selectedGoal.name} • Cuisine: ${plannerState.selectedCuisine}", style=MaterialTheme.typography.bodySmall)
                    }
                }

                // Stats row
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp), modifier=Modifier.fillMaxWidth()) {
                    StatCard("Activities",allWorkouts.filter{!it.isScheduled}.size.toString(),"Logged this week",Modifier.weight(1f))
                    StatCard("Next Workout",upcomingWk.firstOrNull()?.name?:"None",upcomingWk.firstOrNull()?.let{"scheduled"}?:"Add one below",Modifier.weight(1f))
                }

                // Activity quick-log inline
                Card(Modifier.fillMaxWidth(), shape=RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement=Arrangement.spacedBy(10.dp)) {
                        Text("🏃 Quick Actions", style=MaterialTheme.typography.titleSmall, color=PinkDark, fontWeight=FontWeight.SemiBold)
                        Row(horizontalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth()) {
                            Button(onClick=onScan, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=BlueAccent), shape=RoundedCornerShape(8.dp)) { Text("📷 Scan", style=MaterialTheme.typography.bodySmall, maxLines=1) }
                            Button(onClick=onMenu, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFE8F0FE),contentColor=Color(0xFF1F5EA8)), shape=RoundedCornerShape(8.dp)) { Text("🍽️ Browse Menu", style=MaterialTheme.typography.bodySmall, maxLines=1) }
                            Button(onClick=onRecipeSearch, modifier=Modifier.weight(1f), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFFE8F0FE),contentColor=Color(0xFF1F5EA8)), shape=RoundedCornerShape(8.dp)) { Text("🔍 Browse Recipe", style=MaterialTheme.typography.bodySmall, maxLines=1) }
                        }
                    }
                }

                // Water + Steps
                Row(horizontalArrangement=Arrangement.spacedBy(10.dp), modifier=Modifier.fillMaxWidth()) {
                    Card(Modifier.weight(1f), shape=RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(14.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                            Text("💧 Water", style=MaterialTheme.typography.titleSmall, fontWeight=FontWeight.SemiBold, color=PinkDark)
                            Text("$todayWater ml", style=MaterialTheme.typography.headlineSmall, color=BlueAccent, fontWeight=FontWeight.Bold)
                            Row(horizontalArrangement=Arrangement.spacedBy(4.dp), modifier=Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick={waterVm.add(250)},modifier=Modifier.weight(1f),contentPadding=PaddingValues(4.dp),shape=RoundedCornerShape(8.dp)){Text("+250",style=MaterialTheme.typography.labelSmall)}
                                OutlinedButton(onClick={waterVm.add(500)},modifier=Modifier.weight(1f),contentPadding=PaddingValues(4.dp),shape=RoundedCornerShape(8.dp)){Text("+500",style=MaterialTheme.typography.labelSmall)}
                            }
                            TextButton(onClick=onWater,modifier=Modifier.fillMaxWidth(),contentPadding=PaddingValues(0.dp)){Text("Full tracker →",style=MaterialTheme.typography.labelSmall)}
                        }
                    }
                    Card(Modifier.weight(1f), shape=RoundedCornerShape(16.dp)) {
                        Column(Modifier.padding(14.dp), verticalArrangement=Arrangement.spacedBy(8.dp)) {
                            Text("👟 Steps", style=MaterialTheme.typography.titleSmall, fontWeight=FontWeight.SemiBold, color=PinkDark)
                            Text("$steps", style=MaterialTheme.typography.headlineSmall, color=GreenAccent, fontWeight=FontWeight.Bold)
                            Text(
                                when {
                                    healthConnectVm.needsInstallOrUpdate(context) -> "Tap Refresh to install"
                                    !healthConnectVm.isAvailable(context) -> "Health Connect unavailable"
                                    !hasNormalPerms -> "Activity permission needed"
                                    !hasHCPerms -> "Tap Refresh to grant access"
                                    else -> "Today's step count"
                                },
                                style=MaterialTheme.typography.bodySmall, color=SubtleGray)
                            
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Button(onClick={
                                    val needsInstall=healthConnectVm.needsInstallOrUpdate(context)
                                    val available=healthConnectVm.isAvailable(context)
                                    when { needsInstall||!available->healthConnectVm.openHealthConnectInPlayStore(context); !hasNormalPerms->normalLauncher.launch(healthConnectVm.normalPermissions.toTypedArray()); !hasHCPerms->hcLauncher.launch(healthConnectVm.healthConnectPermissions); else->healthConnectVm.refreshSteps(context) }
                                }, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=PinkPrimary), shape=RoundedCornerShape(8.dp), contentPadding=PaddingValues(4.dp)) { Text("Refresh Steps",style=MaterialTheme.typography.labelSmall) }
                            }
                        }
                    }
                }

                // Workout shortcut
                Button(onClick=onWorkout, modifier=Modifier.fillMaxWidth(), colors=ButtonDefaults.buttonColors(containerColor=Color(0xFF28A745)), shape=RoundedCornerShape(12.dp)) {
                    Icon(Icons.Default.FitnessCenter, null); Text("  🏋️ Log Workout / Activity", fontWeight=FontWeight.SemiBold)
                }

                // Recent meals
                Text("Recent Meals", style=MaterialTheme.typography.titleMedium, fontWeight=FontWeight.SemiBold)
            }
        }

        if(mealState.meals.isEmpty()) {
            item {
                Card(Modifier.fillMaxWidth().padding(horizontal=16.dp), shape=RoundedCornerShape(16.dp)) {
                    Text("No meals yet. Use 'Browse Menu', 'Scan Label', or 'Browse Recipe' to get started.",
                        Modifier.padding(16.dp), color=SubtleGray)
                }
            }
        } else {
            items(mealState.meals.take(8), key={it.id}) { meal ->
                Card(Modifier.fillMaxWidth().padding(horizontal=16.dp, vertical=4.dp), shape=RoundedCornerShape(12.dp)) {
                    Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement=Arrangement.SpaceBetween, verticalAlignment=Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement=Arrangement.spacedBy(3.dp)) {
                            Text(meal.name, fontWeight=FontWeight.SemiBold, maxLines=1, overflow=TextOverflow.Ellipsis)
                            Text("${meal.mealType} • ${meal.cuisine}", style=MaterialTheme.typography.bodySmall, color=SubtleGray)
                            if (meal.protein > 0 || meal.carbs > 0 || meal.fat > 0) {
                                Text("P:${meal.protein}g  C:${meal.carbs}g  F:${meal.fat}g",
                                    style=MaterialTheme.typography.labelSmall, color=SubtleGray)
                            }
                        }
                        Column(horizontalAlignment=Alignment.End, verticalArrangement=Arrangement.spacedBy(4.dp)) {
                            Text("${meal.calories} kcal", fontWeight=FontWeight.Bold, color=BlueAccent,
                                style=MaterialTheme.typography.bodyMedium)
                            TextButton(onClick={mealsVm.deleteMeal(meal)},
                                contentPadding=PaddingValues(horizontal=4.dp, vertical=0.dp)) {
                                Text("Remove", style=MaterialTheme.typography.labelSmall, color=MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(title:String, value:String, sub:String, modifier:Modifier=Modifier) {
    Card(modifier, shape=RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(14.dp), verticalArrangement=Arrangement.spacedBy(4.dp)) {
            Text(title, style=MaterialTheme.typography.titleSmall, color=PinkDark, fontWeight=FontWeight.SemiBold)
            Text(value, style=MaterialTheme.typography.headlineSmall, fontWeight=FontWeight.Bold, maxLines=1, overflow=TextOverflow.Ellipsis)
            Text(sub, style=MaterialTheme.typography.bodySmall, color=SubtleGray)
        }
    }
}
