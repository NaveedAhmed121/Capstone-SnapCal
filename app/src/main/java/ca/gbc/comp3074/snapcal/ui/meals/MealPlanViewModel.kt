package ca.gbc.comp3074.snapcal.ui.meals

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MealPlanViewModel : ViewModel() {

    data class PlannedMeal(
        val meal: DemoMeals.Meal,
        val frequencyPerWeek: Int
    )

    data class ShoppingItem(
        val name: String,
        val quantity: Double,
        val unit: String
    )

    data class UiState(
        val plannedMeals: List<PlannedMeal> = emptyList(),
        val shoppingByCategory: Map<DemoMeals.Category, List<ShoppingItem>> = emptyMap(),
        val adHocShoppingItems: List<String> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun addOrUpdateMeal(meal: DemoMeals.Meal, frequencyPerWeek: Int) {
        val freq = frequencyPerWeek.coerceIn(1, 7)
        _uiState.update { s ->
            val idx = s.plannedMeals.indexOfFirst { it.meal.id == meal.id }
            val updated = if (idx >= 0) {
                s.plannedMeals.toMutableList().apply { this[idx] = PlannedMeal(meal, freq) }
            } else {
                s.plannedMeals + PlannedMeal(meal, freq)
            }
            s.copy(plannedMeals = updated)
        }
    }

    fun removeMeal(mealId: Int) {
        _uiState.update { s -> s.copy(plannedMeals = s.plannedMeals.filterNot { it.meal.id == mealId }) }
        generateShoppingList()
    }

    fun clearPlan() {
        _uiState.update { UiState() }
    }

    fun generateShoppingList() {
        val plan = _uiState.value.plannedMeals
        if (plan.isEmpty()) {
            _uiState.update { it.copy(shoppingByCategory = emptyMap()) }
            return
        }

        val map = linkedMapOf<DemoMeals.Category, LinkedHashMap<String, ShoppingItem>>()

        for (pm in plan) {
            for (ing in pm.meal.ingredients) {
                val qty = ing.quantity * pm.frequencyPerWeek
                val catMap = map.getOrPut(ing.category) { linkedMapOf() }
                val existing = catMap[ing.name]
                if (existing == null) {
                    catMap[ing.name] = ShoppingItem(ing.name, qty, ing.unit)
                } else {
                    // Same unit in our demo data; just sum.
                    catMap[ing.name] = existing.copy(quantity = existing.quantity + qty)
                }
            }
        }

        val normalized = map.mapValues { (_, items) -> items.values.toList() }
        _uiState.update { it.copy(shoppingByCategory = normalized) }
    }

    fun clearShoppingList() {
        _uiState.update { it.copy(shoppingByCategory = emptyMap(), adHocShoppingItems = emptyList()) }
    }

    fun addToShoppingList(itemName: String) {
        _uiState.update { it.copy(adHocShoppingItems = it.adHocShoppingItems + itemName) }
    }

    fun removeAdHocShoppingItem(itemName: String) {
        _uiState.update { it.copy(adHocShoppingItems = it.adHocShoppingItems - itemName) }
    }

    fun loadDemoPlan() {
        val picks = listOf(
            1 to 2,
            8 to 3,
            14 to 2,
            21 to 4,
            10 to 2
        )
        val planned = picks.mapNotNull { (id, freq) ->
            DemoMeals.all.find { it.id == id }?.let { PlannedMeal(it, freq) }
        }
        _uiState.update { it.copy(plannedMeals = planned) }
    }

    fun loadDemoAndGenerateShopping() {
        loadDemoPlan()
        generateShoppingList()
    }

    fun weeklyCalories(): Int {
        return _uiState.value.plannedMeals.sumOf { it.meal.calories * it.frequencyPerWeek }
    }

    fun weeklyMealCount(): Int {
        return _uiState.value.plannedMeals.sumOf { it.frequencyPerWeek }
    }
}
