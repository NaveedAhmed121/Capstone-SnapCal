package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.gbc.comp3074.snapcal.data.remote.NutritionResult
import ca.gbc.comp3074.snapcal.data.remote.NutritionSearchService
import ca.gbc.comp3074.snapcal.ui.theme.*
import kotlinx.coroutines.launch

class ManualMealViewModel : ViewModel() {
    private val service = NutritionSearchService()
    var results   by mutableStateOf<List<NutritionResult>>(emptyList())
    var isLoading by mutableStateOf(false)

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            results = service.searchFood(query)
            isLoading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualMealScreen(
    onBack: () -> Unit,
    onSave: (name: String, calories: Int, protein: Int, carbs: Int, fat: Int,
             mealType: String, cuisine: String, goalType: String) -> Unit,
    nutritionVm: ManualMealViewModel = viewModel()
) {
    var name     by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein  by remember { mutableStateOf("") }
    var carbs    by remember { mutableStateOf("") }
    var fat      by remember { mutableStateOf("") }

    val mealTypes  = listOf("Breakfast","Lunch","Dinner","Snack")
    val cuisines   = listOf("American","Mediterranean","Italian","Chinese","Japanese","Mexican","Indian","Pakistani","Thai","Korean","Lebanese","Turkish","Other")
    val goalTypes  = listOf("Lose","Maintain","Gain")

    var selectedMealType by remember { mutableStateOf(mealTypes.first()) }
    var selectedCuisine  by remember { mutableStateOf(cuisines.first()) }
    var selectedGoalType by remember { mutableStateOf("Maintain") }

    var mealTypeExpanded by remember { mutableStateOf(false) }
    var cuisineExpanded  by remember { mutableStateOf(false) }
    var goalExpanded     by remember { mutableStateOf(false) }
    var showNutrition    by remember { mutableStateOf(false) }

    fun toInt(v: String) = v.trim().toIntOrNull() ?: 0

    Column(Modifier.fillMaxSize()) {
        // Header
        Box(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(PinkLight, PeachAccent))).padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                Column {
                    Text("✏️ Add Meal Manually", style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Enter details or search for nutrition info",
                        style = MaterialTheme.typography.bodySmall, color = Color.White.copy(0.85f))
                }
            }
        }

        Column(
            Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Meal name + nutrition search
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Meal Details", fontWeight = FontWeight.SemiBold, color = PinkDark,
                        style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = name, onValueChange = { name = it },
                        label = { Text("Meal name") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        ),
                        trailingIcon = {
                            IconButton(onClick = { nutritionVm.search(name); showNutrition = true }) {
                                Icon(Icons.Default.Search, "Search nutrition", tint = PinkPrimary)
                            }
                        }
                    )
                    // Nutrition search results
                    if (showNutrition) {
                        if (nutritionVm.isLoading) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp), color = PinkPrimary, strokeWidth = 2.dp)
                                Text("Searching nutrition database...", style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                            }
                        } else if (nutritionVm.results.isNotEmpty()) {
                            Text("Tap a result to auto-fill:", style = MaterialTheme.typography.labelSmall, color = PinkDark)
                            nutritionVm.results.forEach { r ->
                                OutlinedButton(
                                    onClick = {
                                        name     = r.name
                                        calories = r.calories.toString()
                                        protein  = r.protein.toString()
                                        carbs    = r.carbs.toString()
                                        fat      = r.fat.toString()
                                        showNutrition = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Column(Modifier.fillMaxWidth()) {
                                        Text(r.name, fontWeight = FontWeight.Medium,
                                            style = MaterialTheme.typography.bodySmall)
                                        Text("${r.calories} kcal  •  P:${r.protein}g  C:${r.carbs}g  F:${r.fat}g  •  ${r.quantity}",
                                            style = MaterialTheme.typography.labelSmall, color = SubtleGray)
                                    }
                                }
                            }
                        } else if (!nutritionVm.isLoading) {
                            Text("No results. Enter calories manually below.",
                                style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                        }
                    }
                }
            }

            // Calories & Macros
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Nutrition (per serving)", fontWeight = FontWeight.SemiBold, color = PinkDark,
                        style = MaterialTheme.typography.titleSmall)
                    OutlinedTextField(
                        value = calories, onValueChange = { calories = it.filter(Char::isDigit) },
                        label = { Text("Calories (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                            focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                        )
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf(Triple("Protein (g)", protein) { v: String -> protein = v },
                               Triple("Carbs (g)",   carbs)   { v: String -> carbs   = v },
                               Triple("Fat (g)",     fat)     { v: String -> fat     = v }
                        ).forEach { (label, value, onChange) ->
                            OutlinedTextField(
                                value = value, onValueChange = { onChange(it.filter(Char::isDigit)) },
                                label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.weight(1f), shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                                    focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Dropdowns
            Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Categorise", fontWeight = FontWeight.SemiBold, color = PinkDark,
                        style = MaterialTheme.typography.titleSmall)
                    DropdownField("Meal Type", selectedMealType, mealTypeExpanded,
                        { mealTypeExpanded = !mealTypeExpanded }, mealTypes) { selectedMealType = it; mealTypeExpanded = false }
                    DropdownField("Cuisine", selectedCuisine, cuisineExpanded,
                        { cuisineExpanded = !cuisineExpanded }, cuisines) { selectedCuisine = it; cuisineExpanded = false }
                    DropdownField("Goal", selectedGoalType, goalExpanded,
                        { goalExpanded = !goalExpanded }, goalTypes) { selectedGoalType = it; goalExpanded = false }
                }
            }

            Button(
                onClick = {
                    onSave(name.ifBlank { "Meal" }, toInt(calories), toInt(protein),
                           toInt(carbs), toInt(fat), selectedMealType, selectedCuisine, selectedGoalType)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                enabled = name.isNotBlank()
            ) {
                Text("💾 Save Meal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(
    label: String, value: String, expanded: Boolean,
    onExpandedChange: () -> Unit, options: List<String>, onSelected: (String) -> Unit
) {
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { onExpandedChange() }) {
        OutlinedTextField(
            value = value, onValueChange = {}, readOnly = true, label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.Black, unfocusedTextColor = Color.Black,
                focusedContainerColor = Color.White, unfocusedContainerColor = Color.White
            )
        )
        DropdownMenu(expanded = expanded, onDismissRequest = onExpandedChange) {
            options.forEach { opt ->
                DropdownMenuItem(text = { Text(opt) }, onClick = { onSelected(opt) })
            }
        }
    }
}
