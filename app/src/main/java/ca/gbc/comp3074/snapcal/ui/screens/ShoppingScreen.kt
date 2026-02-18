package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import ca.gbc.comp3074.snapcal.ui.meals.DemoMeals
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    mealPlanVm: MealPlanViewModel,
    onGoPlanner: () -> Unit
) {
    val state by mealPlanVm.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🛒 Shopping List") },
                actions = {
                    if (state.shoppingByCategory.isNotEmpty()) {
                        Button(onClick = { mealPlanVm.clearShoppingList() }) { Text("Clear") }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (state.shoppingByCategory.isEmpty()) {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("No shopping list yet", fontWeight = FontWeight.SemiBold)
                        Text("Generate your shopping list from your meal plan.")
                        Button(onClick = {
                            // If they already have a plan, generate; else go planner
                            if (state.plannedMeals.isNotEmpty()) mealPlanVm.generateShoppingList() else onGoPlanner()
                        }) {
                            Text(if (state.plannedMeals.isNotEmpty()) "Generate Now" else "Go to Meal Plan")
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxSize()) {
                    items(state.shoppingByCategory.entries.toList(), key = { it.key.name }) { (cat, items) ->
                        Card(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("${catIcon(cat)} ${cat.name.replaceFirstChar { it.uppercase() }}", fontWeight = FontWeight.SemiBold)
                                items.forEach { it2 ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(it2.name)
                                        Text("${pretty(it2.quantity)} ${it2.unit}", style = MaterialTheme.typography.bodySmall)
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

private fun catIcon(cat: DemoMeals.Category): String = when (cat) {
    DemoMeals.Category.meat -> "🥩"
    DemoMeals.Category.seafood -> "🐟"
    DemoMeals.Category.dairy -> "🥛"
    DemoMeals.Category.vegetables -> "🥕"
    DemoMeals.Category.fruits -> "🍎"
    DemoMeals.Category.pantry -> "🏪"
}

private fun pretty(v: Double): String {
    val i = v.toInt()
    return if (v == i.toDouble()) i.toString() else String.format("%.1f", v)
}
