package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import ca.gbc.comp3074.snapcal.data.remote.OnlineRecipe
import ca.gbc.comp3074.snapcal.data.remote.TheMealDbService
import ca.gbc.comp3074.snapcal.ui.theme.*
import ca.gbc.comp3074.snapcal.viewmodel.MealsViewModel
import kotlinx.coroutines.launch

class RecipeSearchViewModel : ViewModel() {
    private val service = TheMealDbService()
    var results by mutableStateOf<List<OnlineRecipe>>(emptyList())
    var isLoading by mutableStateOf(false)
    var statusMsg by mutableStateOf("")
    var importedMsg by mutableStateOf("")

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            isLoading = true
            statusMsg = "Searching..."
            results = emptyList()
            var found = service.searchByName(query)
            if (found.isEmpty()) {
                statusMsg = "Trying ingredient search..."
                found = service.searchByIngredient(query)
            }
            results = found
            statusMsg = if (found.isEmpty()) "No results for \"$query\". Try another term." else ""
            isLoading = false
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeSearchScreen(
    mealsVm: MealsViewModel,
    onBack: () -> Unit,
    searchVm: RecipeSearchViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Dinner") }
    var selectedRecipe by remember { mutableStateOf<OnlineRecipe?>(null) }
    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")
    val keyboard = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🌐 Quick Import Recipe") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PinkPrimary, titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search card - plain white background so text is always visible
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Search online recipe database", style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold, color = PinkDark)
                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            label = { Text("Recipe name (e.g. lasagna, butter chicken...)") },
                            trailingIcon = {
                                IconButton(onClick = { keyboard?.hide(); searchVm.search(query) }) {
                                    Icon(Icons.Default.Search, "Search", tint = PinkPrimary)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { keyboard?.hide(); searchVm.search(query) }),
                            // Explicit text + label colors so they always show
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedBorderColor = PinkPrimary,
                                unfocusedBorderColor = Color(0xFFE0E0E0),
                                focusedLabelColor = PinkPrimary,
                                unfocusedLabelColor = SubtleGray
                            )
                        )
                        Button(
                            onClick = { keyboard?.hide(); searchVm.search(query) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary),
                            enabled = query.isNotBlank()
                        ) { Text("Search Recipes", fontWeight = FontWeight.SemiBold) }
                    }
                }
            }

            // Meal type selector
            item {
                Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Add imported recipe as:", style = MaterialTheme.typography.labelMedium, color = PinkDark)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            mealTypes.forEach { type ->
                                FilterChip(
                                    selected = selectedMealType == type,
                                    onClick = { selectedMealType = type },
                                    label = { Text(type, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
            }

            // Import success message
            if (searchVm.importedMsg.isNotEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))) {
                        Text(searchVm.importedMsg, Modifier.padding(14.dp),
                            color = Color(0xFF155724), fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Loading / status / results
            when {
                searchVm.isLoading -> item {
                    Box(Modifier.fillMaxWidth().padding(24.dp), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            CircularProgressIndicator(color = PinkPrimary)
                            Text(searchVm.statusMsg, style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                        }
                    }
                }
                searchVm.statusMsg.isNotEmpty() && !searchVm.isLoading -> item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3CD))) {
                        Text(searchVm.statusMsg, Modifier.padding(14.dp), color = Color(0xFF856404))
                    }
                }
                else -> items(searchVm.results) { recipe ->
                    Card(
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp),
                        onClick = { selectedRecipe = recipe }
                    ) {
                        Row(Modifier.fillMaxWidth().padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("🍝 ${recipe.name}", fontWeight = FontWeight.SemiBold, maxLines = 2)
                                Text("${recipe.category} • ${recipe.area}",
                                    style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                                Text("${recipe.ingredients.size} ingredients",
                                    style = MaterialTheme.typography.bodySmall, color = BlueAccent)
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    mealsVm.addMeal(recipe.name, estimateCal(recipe.name), 0, 0, 0,
                                        selectedMealType, recipe.area.ifBlank { "International" }, "Maintain")
                                    searchVm.importedMsg = "✅ '${recipe.name}' added to $selectedMealType!"
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = BlueAccent),
                                shape = RoundedCornerShape(8.dp)
                            ) { Text("Import") }
                        }
                    }
                }
            }

            // Empty state
            if (!searchVm.isLoading && searchVm.results.isEmpty() && searchVm.statusMsg.isEmpty() && searchVm.importedMsg.isEmpty()) {
                item {
                    Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text("💡 Tips", fontWeight = FontWeight.SemiBold, color = PinkDark)
                            Text("• Search by dish name: \"pasta\", \"curry\", \"salad\"", style = MaterialTheme.typography.bodySmall)
                            Text("• Search by ingredient: \"chicken\", \"salmon\", \"oats\"", style = MaterialTheme.typography.bodySmall)
                            Text("• Powered by TheMealDB — 300+ recipes", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }

    // Recipe detail dialog
    selectedRecipe?.let { recipe ->
        AlertDialog(
            onDismissRequest = { selectedRecipe = null },
            title = { Text(recipe.name, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("${recipe.category} • ${recipe.area}",
                        style = MaterialTheme.typography.bodySmall, color = SubtleGray)
                    if (recipe.ingredients.isNotEmpty()) {
                        Text("Ingredients:", fontWeight = FontWeight.SemiBold)
                        recipe.ingredients.take(12).forEach { (name, measure) ->
                            Text("• $measure $name", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    if (recipe.instructions.isNotBlank()) {
                        Text("Instructions:", fontWeight = FontWeight.SemiBold)
                        Text(recipe.instructions.take(400).trimEnd() + "…",
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mealsVm.addMeal(recipe.name, estimateCal(recipe.name), 0, 0, 0,
                            selectedMealType, recipe.area.ifBlank { "International" }, "Maintain")
                        searchVm.importedMsg = "✅ '${recipe.name}' added to $selectedMealType!"
                        selectedRecipe = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                ) { Text("📅 Add to $selectedMealType") }
            },
            dismissButton = { TextButton(onClick = { selectedRecipe = null }) { Text("Close") } }
        )
    }
}

private fun estimateCal(name: String): Int {
    val n = name.lowercase()
    return when {
        n.contains("salad")  -> 320
        n.contains("soup")   -> 280
        n.contains("burger") -> 650
        n.contains("pasta") || n.contains("lasagna") -> 520
        n.contains("chicken") -> 420
        n.contains("fish") || n.contains("salmon") -> 380
        n.contains("rice") -> 380
        n.contains("cake") || n.contains("dessert") -> 450
        else -> 400
    }
}
