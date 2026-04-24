package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ca.gbc.comp3074.snapcal.ui.meals.DemoMeals
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel
import ca.gbc.comp3074.snapcal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingScreen(
    mealPlanVm: MealPlanViewModel,
    onBack: () -> Unit,
    onGoPlanner: () -> Unit
) {
    val state by mealPlanVm.uiState.collectAsState()
    var extraItem by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Pink gradient header with back button
        Box(
            Modifier.fillMaxWidth()
                .background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                        Text(
                            "🛒 Shopping List",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = Color.White
                        )
                    }
                    
                    if (state.shoppingByCategory.isNotEmpty() || state.adHocShoppingItems.isNotEmpty()) {
                        IconButton(onClick = { mealPlanVm.clearShoppingList() }) {
                            Icon(Icons.Default.Delete, "Clear", tint = Color.White)
                        }
                    }
                }
                Text("Ingredients generated from your meal plan",
                    style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f),
                    modifier = Modifier.padding(start = 40.dp))
            }
        }

        LazyColumn(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Plan summary & Generate card
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Plan Summary", style = MaterialTheme.typography.titleSmall, color = PinkDark, fontWeight = FontWeight.SemiBold)
                        Text(
                            "You have ${state.plannedMeals.size} meals in your current plan. Generate the list to see what you need to buy.",
                            style = MaterialTheme.typography.bodySmall, color = SubtleGray
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(
                                onClick = {
                                    if (state.plannedMeals.isNotEmpty()) mealPlanVm.generateShoppingList() else onGoPlanner()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                            ) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(if (state.plannedMeals.isNotEmpty()) "Update List" else "Go to Planner")
                            }
                        }
                    }
                }
            }

            // Quick add item
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("➕ Add Extra Item", style = MaterialTheme.typography.titleSmall, color = PinkDark, fontWeight = FontWeight.SemiBold)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = extraItem,
                                onValueChange = { extraItem = it },
                                label = { Text("e.g. Eggs, Milk...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                singleLine = true
                            )
                            Button(
                                onClick = {
                                    if (extraItem.isNotBlank()) {
                                        mealPlanVm.addToShoppingList(extraItem)
                                        extraItem = ""
                                    }
                                },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent)
                            ) { Text("Add") }
                        }
                    }
                }
            }

            if (state.shoppingByCategory.isEmpty() && state.adHocShoppingItems.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(
                            Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🛒", fontSize = 48.sp)
                            Text("Your list is empty", fontWeight = FontWeight.SemiBold)
                            Text("Add meals to your planner first.", style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                        }
                    }
                }
            } else {
                // Ad-hoc items
                if (state.adHocShoppingItems.isNotEmpty()) {
                    item {
                        Text("Other Items", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    }
                    items(state.adHocShoppingItems) { itemName ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(itemName, fontWeight = FontWeight.Medium)
                                IconButton(onClick = { mealPlanVm.removeAdHocShoppingItem(itemName) }) {
                                    Icon(Icons.Default.Delete, "Remove", tint = Color(0xFFDC3545), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }

                // Categorized items
                state.shoppingByCategory.forEach { (cat, itemsForCategory) ->
                    item {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "${catIcon(cat)} ${cat.name.replaceFirstChar { it.uppercase() }}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = PinkDark
                        )
                    }
                    items(itemsForCategory) { shoppingItem ->
                        Card(
                            Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(shoppingItem.name, fontWeight = FontWeight.Medium)
                                Text(
                                    "${pretty(shoppingItem.quantity)} ${shoppingItem.unit}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PinkPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            
            item { Spacer(Modifier.height(24.dp)) }
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
