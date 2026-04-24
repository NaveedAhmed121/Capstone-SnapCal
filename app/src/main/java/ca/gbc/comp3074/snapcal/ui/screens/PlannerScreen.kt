package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import ca.gbc.comp3074.snapcal.ui.meals.DemoMeals
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel
import ca.gbc.comp3074.snapcal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    mealPlanVm: MealPlanViewModel,
    onBack: () -> Unit,
    onGoShopping: () -> Unit,
    onGoMenu: () -> Unit
) {
    val state by mealPlanVm.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // Pink gradient header with back button
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                    }
                    Text(
                        "📅 Weekly Plan",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold, color = Color.White
                    )
                }
                
                // Summary chips
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(start = 8.dp)) {
                    SummaryChip("🍽️ Meals", mealPlanVm.weeklyMealCount().toString())
                    SummaryChip("🔥 kcal/week", mealPlanVm.weeklyCalories().toString())
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Goal selector - sliding LazyRow
            item {
                Column(Modifier.padding(horizontal = 16.dp).padding(top = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Goal", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold, color = PinkDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(DemoMeals.GoalType.entries) { goal ->
                            FilterChip(
                                selected = state.selectedGoal == goal,
                                onClick = { mealPlanVm.setGoal(goal) },
                                label = { Text(goal.name) }
                            )
                        }
                    }
                }
            }

            // Cuisine selector - sliding LazyRow
            item {
                Column(Modifier.padding(horizontal = 16.dp).padding(top = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Cuisine", style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold, color = PinkDark)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = state.selectedCuisine == "All",
                                onClick = { mealPlanVm.setCuisine("All") },
                                label = { Text("All Cuisines") }
                            )
                        }
                        items(DemoMeals.availableCuisines()) { cuisine ->
                            FilterChip(
                                selected = state.selectedCuisine == cuisine,
                                onClick = { mealPlanVm.setCuisine(cuisine) },
                                label = { Text(cuisine) }
                            )
                        }
                    }
                }
            }

            // Action buttons
            item {
                Column(Modifier.padding(horizontal = 16.dp).padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = onGoMenu, modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)) {
                            Text("🍽️ Browse Menu")
                        }
                        Button(onClick = { mealPlanVm.loadDemoPlan() }, modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)) {
                            Text("✨ Auto Fill")
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(onClick = { mealPlanVm.generateShoppingList(); onGoShopping() },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Text("🛒 Shopping List")
                        }
                        OutlinedButton(onClick = { mealPlanVm.clearPlan() },
                            modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp)) {
                            Text("🗑️ Clear Plan")
                        }
                    }
                }
            }

            // Planned meals section header
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Planned Meals", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold)
                    if (state.plannedMeals.isNotEmpty()) {
                        Text("${state.plannedMeals.size} meals",
                            style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                    }
                }
            }

            // Empty state
            if (state.plannedMeals.isEmpty()) {
                item {
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("📋", fontSize = 48.sp)
                            Text("No meals planned yet", fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium)
                            Text(
                                "Go to the Menu tab, tap any meal and choose 'Add to Plan'.",
                                style = MaterialTheme.typography.bodySmall, color = SubtleGray
                            )
                            Button(onClick = onGoMenu,
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                                shape = RoundedCornerShape(12.dp)) {
                                Text("Open Menu")
                            }
                        }
                    }
                }
            } else {
                items(state.plannedMeals, key = { it.meal.id }) { pm ->
                    Card(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("${pm.meal.icon} ${pm.meal.name}",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.titleSmall)
                                Text(
                                    "${pm.meal.type.name.replaceFirstChar { it.uppercase() }} • ${pm.meal.cuisine}",
                                    style = MaterialTheme.typography.bodySmall, color = SubtleGray
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text("${pm.frequencyPerWeek}×/week",
                                        style = MaterialTheme.typography.bodySmall, color = PinkPrimary,
                                        fontWeight = FontWeight.SemiBold)
                                    Text("${pm.meal.calories * pm.frequencyPerWeek} kcal total",
                                        style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                                }
                            }
                            IconButton(onClick = { mealPlanVm.removeMeal(pm.meal.id) }) {
                                Icon(Icons.Default.Delete, "Remove",
                                    tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(label: String, value: String) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(0.9f))
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, style = MaterialTheme.typography.labelSmall, color = PinkDark)
            Text(value, fontWeight = FontWeight.Bold, color = PinkPrimary,
                style = MaterialTheme.typography.bodyMedium)
        }
    }
}
