package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(
    mealPlanVm: MealPlanViewModel,
    onGoShopping: () -> Unit,
    onGoMenu: () -> Unit
) {
    val state by mealPlanVm.uiState.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("📅 Weekly Meal Plan") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Weekly Overview", fontWeight = FontWeight.SemiBold)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Card(Modifier.weight(1f)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Weekly Calories", style = MaterialTheme.typography.bodySmall)
                                Text(mealPlanVm.weeklyCalories().toString(), style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                        Card(Modifier.weight(1f)) {
                            Column(Modifier.padding(12.dp)) {
                                Text("Planned Meals", style = MaterialTheme.typography.bodySmall)
                                Text(mealPlanVm.weeklyMealCount().toString(), style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(onClick = { mealPlanVm.clearPlan() }, modifier = Modifier.weight(1f)) {
                            Text("Clear")
                        }
                        Button(onClick = { mealPlanVm.loadDemoPlan() }, modifier = Modifier.weight(1f)) {
                            Text("Demo Plan")
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = {
                                mealPlanVm.generateShoppingList();
                                onGoShopping()
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Generate Shopping") }
                        Button(
                            onClick = {
                                mealPlanVm.loadDemoAndGenerateShopping();
                                onGoShopping()
                            },
                            modifier = Modifier.weight(1f)
                        ) { Text("Demo + List") }
                    }
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Your Weekly Meal Plan", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.padding(top = 8.dp))

                    if (state.plannedMeals.isEmpty()) {
                        Text("No meals planned yet. Go to Menu to add meals.")
                        Spacer(Modifier.padding(top = 8.dp))
                        Button(onClick = onGoMenu) { Text("Open Menu") }
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.plannedMeals, key = { it.meal.id }) { pm ->
                                Card(Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(Modifier.weight(1f)) {
                                            Text("${pm.meal.icon} ${pm.meal.name}", fontWeight = FontWeight.SemiBold)
                                            Text("${pm.frequencyPerWeek}x/week • ${pm.meal.calories * pm.frequencyPerWeek} kcal", style = MaterialTheme.typography.bodySmall)
                                        }
                                        Button(onClick = { mealPlanVm.removeMeal(pm.meal.id) }) { Text("Remove") }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
