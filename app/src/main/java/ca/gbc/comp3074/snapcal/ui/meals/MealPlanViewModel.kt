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
        val selectedGoal: DemoMeals.GoalType = DemoMeals.GoalType.Maintain,
        val selectedCuisine: String = "All",
        val plannedMeals: List<PlannedMeal> = emptyList(),
        val shoppingByCategory: Map<DemoMeals.Category, List<ShoppingItem>> = emptyMap(),
        val adHocShoppingItems: List<String> = emptyList()
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    fun setGoal(goalType: DemoMeals.GoalType) {
        _uiState.update { it.copy(selectedGoal = goalType) }
    }

    fun setCuisine(cuisine: String) {
        _uiState.update { it.copy(selectedCuisine = cuisine) }
    }

    fun suggestedMeals(type: DemoMeals.MealType? = null): List<DemoMeals.Meal> {
        val state = _uiState.value
        return DemoMeals.getFilteredMeals(
            type = type,
            cuisine = state.selectedCuisine.takeIf { it != "All" },
            goalType = state.selectedGoal
        )
    }

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

    fun addCustomMeal(name: String, calories: Int, type: DemoMeals.MealType, frequency: Int) {
        val customMeal = DemoMeals.Meal(
            id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
            name = name,
            type = type,
            cuisine = "Custom",
            goalType = _uiState.value.selectedGoal,
            calories = calories,
            icon = "🍽️",
            ingredients = emptyList(),
            recipeSteps = listOf("Custom meal entry")
        )
        addOrUpdateMeal(customMeal, frequency)
    }

    fun removeMeal(mealId: Int) {
        _uiState.update { s -> s.copy(plannedMeals = s.plannedMeals.filterNot { it.meal.id == mealId }) }
        generateShoppingList()
    }

    fun clearPlan() {
        _uiState.update { it.copy(plannedMeals = emptyList(), shoppingByCategory = emptyMap(), adHocShoppingItems = emptyList()) }
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
                catMap[ing.name] = if (existing == null) {
                    ShoppingItem(ing.name, qty, ing.unit)
                } else {
                    existing.copy(quantity = existing.quantity + qty)
                }
            }
        }
        _uiState.update { it.copy(shoppingByCategory = map.mapValues { entry -> entry.value.values.toList() }) }
    }

    fun clearShoppingList() {
        _uiState.update { it.copy(shoppingByCategory = emptyMap(), adHocShoppingItems = emptyList()) }
    }

    fun addToShoppingList(itemName: String) {
        val clean = itemName.trim()
        if (clean.isNotEmpty()) {
            _uiState.update { it.copy(adHocShoppingItems = it.adHocShoppingItems + clean) }
        }
    }

    fun removeAdHocShoppingItem(itemName: String) {
        _uiState.update { it.copy(adHocShoppingItems = it.adHocShoppingItems - itemName) }
    }

    fun loadDemoPlan() {
        val targetGoal = _uiState.value.selectedGoal
        val picks = DemoMeals.all.filter { it.goalType == targetGoal }.take(4)
        val planned = picks.mapIndexed { index, meal ->
            PlannedMeal(meal = meal, frequencyPerWeek = if (index == 0) 3 else 2)
        }
        _uiState.update { it.copy(plannedMeals = planned) }
    }

    fun loadDemoAndGenerateShopping() {
        loadDemoPlan()
        generateShoppingList()
    }

    fun weeklyCalories(): Int = _uiState.value.plannedMeals.sumOf { it.meal.calories * it.frequencyPerWeek }
    fun weeklyMealCount(): Int = _uiState.value.plannedMeals.sumOf { it.frequencyPerWeek }
}
