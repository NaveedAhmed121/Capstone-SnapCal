package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.model.MealLog
import ca.gbc.comp3074.snapcal.data.repo.MealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MealsUiState(
    val meals: List<MealLog> = emptyList(),
    val todayCalories: Int = 0
)

class MealsViewModel(private val repo: MealRepository) : ViewModel() {

    val uiState: StateFlow<MealsUiState> =
        combine(repo.observeMeals(), repo.observeTodayCalories()) { meals, todayCalories ->
            MealsUiState(meals = meals, todayCalories = todayCalories)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MealsUiState())

    fun addMeal(
        name: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int
    ) {
        viewModelScope.launch {
            repo.addMeal(
                MealLog(
                    name = name.trim(),
                    calories = calories,
                    protein = protein,
                    carbs = carbs,
                    fat = fat
                )
            )
        }
    }
}
