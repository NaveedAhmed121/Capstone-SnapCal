package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import ca.gbc.comp3074.snapcal.ui.meals.DemoMeals
import ca.gbc.comp3074.snapcal.ui.meals.MealPlanViewModel
import ca.gbc.comp3074.snapcal.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    mealPlanVm: MealPlanViewModel,
    onBack: () -> Unit,
    onGoPlanner: () -> Unit,
    onBrowseRecipes: () -> Unit = {}
) {
    val state by mealPlanVm.uiState.collectAsState()
    var filter by remember { mutableStateOf<DemoMeals.MealType?>(null) }
    var selectedMeal by remember { mutableStateOf<DemoMeals.Meal?>(null) }
    var frequency by remember { mutableIntStateOf(1) }
    
    // Custom meal state
    var showCustomDialog by remember { mutableStateOf(false) }
    var customName by remember { mutableStateOf("") }
    var customCals by remember { mutableStateOf("") }
    var customType by remember { mutableStateOf(DemoMeals.MealType.breakfast) }
    var customFreq by remember { mutableStateOf("1") }

    val allMeals = remember(filter, state.selectedCuisine, state.selectedGoal) {
        mealPlanVm.suggestedMeals(filter)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCustomDialog = true },
                containerColor = PinkPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Edit, "Add Custom Meal")
            }
        }
    ) { pad ->
        Column(Modifier.fillMaxSize().padding(pad)) {
            // Pink gradient header with back button
            Box(
                Modifier.fillMaxWidth()
                    .background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent)))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                        }
                        Text("🍽️ Meal Menu", style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Text("Browse or add custom meals to your plan",
                        style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f),
                        modifier = Modifier.padding(start = 40.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Goal filter - sliding horizontal
                item {
                    Column(Modifier.padding(horizontal = 16.dp).padding(top = 14.dp),
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

                // Cuisine filter - sliding horizontal
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
                                    label = { Text("All") }
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

                // Meal type filter - sliding horizontal
                item {
                    Column(Modifier.padding(horizontal = 16.dp).padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Meal Type", style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold, color = PinkDark)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            item {
                                FilterChip(
                                    selected = filter == null,
                                    onClick = { filter = null },
                                    label = { Text("All") }
                                )
                            }
                            items(DemoMeals.MealType.entries) { type ->
                                FilterChip(
                                    selected = filter == type,
                                    onClick = { filter = type },
                                    label = { Text(type.name.replaceFirstChar { it.uppercase() }) }
                                )
                            }
                        }
                    }
                }

                // Results count
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(top = 12.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${allMeals.size} meals", style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                        Row {
                            TextButton(onClick = onBrowseRecipes) { Text("🔍 Browse Recipes", color = PinkPrimary) }
                            TextButton(onClick = onGoPlanner) { Text("View Plan →", color = PinkPrimary) }
                        }
                    }
                }

                // Meal cards
                if (allMeals.isEmpty()) {
                    item {
                        Card(Modifier.fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(16.dp)) {
                            Column(Modifier.padding(24.dp).fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("😔", fontSize = 48.sp)
                                Text("No meals match your filters", fontWeight = FontWeight.SemiBold)
                                Text("Try adding a custom meal with the + button",
                                    style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                            }
                        }
                    }
                } else {
                    items(allMeals, key = { it.id }) { meal ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(14.dp),
                            onClick = { selectedMeal = meal; frequency = 1 }
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text("${meal.icon} ${meal.name}", fontWeight = FontWeight.SemiBold,
                                        style = MaterialTheme.typography.titleSmall)
                                    Text(
                                        "${meal.type.name.replaceFirstChar { it.uppercase() }} • ${meal.cuisine}",
                                        style = MaterialTheme.typography.bodySmall, color = SubtleGray
                                    )
                                    Text("${meal.calories} kcal • Goal: ${meal.goalType.name}",
                                        style = MaterialTheme.typography.bodySmall, color = PinkPrimary,
                                        fontWeight = FontWeight.Medium)
                                }
                                FilledIconButton(
                                    onClick = { selectedMeal = meal; frequency = 1 },
                                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = PinkPrimary)
                                ) { Icon(Icons.Default.Add, "Add to plan", tint = Color.White) }
                            }
                        }
                    }
                }
            }
        }
    }

    // Meal detail + add-to-plan dialog
    selectedMeal?.let { meal ->
        AlertDialog(
            onDismissRequest = { selectedMeal = null },
            title = { Text("${meal.icon} ${meal.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Info chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AssistChip(onClick = {}, label = { Text(meal.cuisine) })
                        AssistChip(onClick = {}, label = { Text("${meal.calories} kcal") })
                        AssistChip(onClick = {}, label = { Text(meal.goalType.name) })
                    }
                    if (meal.ingredients.isNotEmpty()) {
                        Text("Ingredients", fontWeight = FontWeight.SemiBold)
                        meal.ingredients.forEach { ing ->
                            Text("• ${ing.name} — ${prettyQty(ing.quantity)} ${ing.unit}",
                                style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (meal.recipeSteps.isNotEmpty()) {
                        Text("Recipe Steps", fontWeight = FontWeight.SemiBold)
                        meal.recipeSteps.forEachIndexed { i, step ->
                            Text("${i + 1}. $step", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    HorizontalDivider()
                    Text("How many times this week?", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = frequency.toString(),
                        onValueChange = { frequency = (it.toIntOrNull() ?: 1).coerceIn(1, 7) },
                        label = { Text("Times per week (1–7)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mealPlanVm.addOrUpdateMeal(meal, frequency)
                        selectedMeal = null
                        onGoPlanner()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) { Text("📅 Add to Plan") }
            },
            dismissButton = { TextButton(onClick = { selectedMeal = null }) { Text("Close") } }
        )
    }

    // Add Custom Meal Dialog
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("✏️ Add Custom Meal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = customName, onValueChange = { customName = it },
                        label = { Text("Meal Name") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        )
                    )
                    OutlinedTextField(
                        value = customCals, onValueChange = { customCals = it.filter(Char::isDigit) },
                        label = { Text("Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        )
                    )
                    
                    Text("Meal Type", style = MaterialTheme.typography.labelMedium)
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        DemoMeals.MealType.entries.forEach { type ->
                            FilterChip(
                                selected = customType == type,
                                onClick = { customType = type },
                                label = { Text(type.name.take(3).uppercase()) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = customFreq, onValueChange = { customFreq = it.filter(Char::isDigit) },
                        label = { Text("Times per week") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        )
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customName.isNotBlank()) {
                            mealPlanVm.addCustomMeal(
                                customName,
                                customCals.toIntOrNull() ?: 0,
                                customType,
                                customFreq.toIntOrNull() ?: 1
                            )
                            showCustomDialog = false
                            customName = ""; customCals = ""; customFreq = "1"
                            onGoPlanner()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                    enabled = customName.isNotBlank()
                ) { Text("Save & Add") }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDialog = false }) { Text("Cancel") }
            }
        )
    }
}

private fun prettyQty(v: Double): String {
    val i = v.toInt()
    return if (v == i.toDouble()) i.toString() else "%.1f".format(v)
}
