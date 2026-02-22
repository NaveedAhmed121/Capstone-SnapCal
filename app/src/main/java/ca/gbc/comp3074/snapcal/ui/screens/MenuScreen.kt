package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ca.gbc.comp3074.snapcal.ui.meals.DemoMeals
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    mealPlanVm: MealPlanViewModel,
    onGoPlanner: () -> Unit
) {
    var filter by remember { mutableStateOf<DemoMeals.MealType?>(null) }
    var selectedMeal by remember { mutableStateOf<DemoMeals.Meal?>(null) }
    var frequency by remember { mutableIntStateOf(1) }

    val allMeals = remember(filter) {
        if (filter == null) DemoMeals.all else DemoMeals.all.filter { it.type == filter }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("🍽️ Healthy Menu") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                FilterChip(selected = filter == null, onClick = { filter = null }, label = { Text("All") })
                FilterChip(
                    selected = filter == DemoMeals.MealType.breakfast,
                    onClick = { filter = DemoMeals.MealType.breakfast },
                    label = { Text("🌅 Breakfast") }
                )
                FilterChip(
                    selected = filter == DemoMeals.MealType.lunch,
                    onClick = { filter = DemoMeals.MealType.lunch },
                    label = { Text("🌞 Lunch") }
                )
                FilterChip(
                    selected = filter == DemoMeals.MealType.dinner,
                    onClick = { filter = DemoMeals.MealType.dinner },
                    label = { Text("🌙 Dinner") }
                )
                FilterChip(
                    selected = filter == DemoMeals.MealType.snack,
                    onClick = { filter = DemoMeals.MealType.snack },
                    label = { Text("🥜 Snack") }
                )
            }

            Spacer(Modifier.width(1.dp))
            Spacer(Modifier.padding(top = 12.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                items(allMeals, key = { it.id }) { meal ->
                    Card(
                        onClick = {
                            selectedMeal = meal
                            frequency = 1
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text("${meal.icon} ${meal.name}", fontWeight = FontWeight.SemiBold)
                                Text(meal.type.name, style = MaterialTheme.typography.bodySmall)
                                Text("${meal.calories} kcal", color = MaterialTheme.colorScheme.primary)
                            }
                            Button(onClick = {
                                selectedMeal = meal
                                frequency = 1
                            }) {
                                Text("Add")
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedMeal != null) {
        val meal = selectedMeal!!
        MealDetailsDialog(
            meal = meal,
            frequency = frequency,
            onFrequencyChange = { frequency = it },
            onDismiss = { selectedMeal = null },
            onAdd = {
                mealPlanVm.addOrUpdateMeal(meal, frequency)
                selectedMeal = null
                onGoPlanner()
            }
        )
    }
}

@Composable
private fun MealDetailsDialog(
    meal: DemoMeals.Meal,
    frequency: Int,
    onFrequencyChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onAdd: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = { Button(onClick = onAdd) { Text("📅 Add to Meal Plan") } },
        dismissButton = { Button(onClick = onDismiss) { Text("Close") } },
        title = { Text("${meal.icon} ${meal.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("${meal.calories} calories", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)

                Text("🛒 Ingredients", fontWeight = FontWeight.SemiBold)
                meal.ingredients.forEach { ing ->
                    Text("• ${ing.name} — ${prettyQty(ing.quantity)} ${ing.unit}")
                }

                Text("👩‍🍳 Recipe", fontWeight = FontWeight.SemiBold)
                meal.recipeSteps.forEachIndexed { i, step ->
                    Text("${i + 1}. $step")
                }

                OutlinedTextField(
                    value = frequency.toString(),
                    onValueChange = { txt ->
                        val v = txt.toIntOrNull() ?: 1
                        onFrequencyChange(v.coerceIn(1, 7))
                    },
                    label = { Text("Times this week (1–7)") },
                    singleLine = true
                )
            }
        }
    )
}

private fun prettyQty(v: Double): String {
    val asInt = v.toInt()
    return if (v == asInt.toDouble()) asInt.toString() else v.toString()
}
