package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.*
import ca.gbc.comp3074.snapcal.data.model.MealLog
import ca.gbc.comp3074.snapcal.data.repo.MealRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class MealsUiState(
    val meals: List<MealLog> = emptyList(),          // all meals (most recent first)
    val todayMeals: List<MealLog> = emptyList(),      // today only
    val todayCalories: Int   = 0,
    val breakfastCalories: Int = 0,
    val lunchCalories: Int   = 0,
    val dinnerCalories: Int  = 0,
    val snackCalories: Int   = 0
)

class MealsViewModel(private val repo: MealRepository) : ViewModel() {

    val uiState: StateFlow<MealsUiState> = combine(
        repo.observeMeals(),
        repo.observeTodayCalories()
    ) { meals, todayCal ->
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayMeals = meals.filter {
            val mealDay = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(Date(it.createdAt))
            mealDay == todayStr
        }
        MealsUiState(
            meals            = meals,
            todayMeals       = todayMeals,
            todayCalories    = todayCal,
            breakfastCalories = todayMeals.filter { it.mealType.equals("Breakfast", true) }.sumOf { it.calories },
            lunchCalories    = todayMeals.filter { it.mealType.equals("Lunch", true) }.sumOf { it.calories },
            dinnerCalories   = todayMeals.filter { it.mealType.equals("Dinner", true) }.sumOf { it.calories },
            snackCalories    = todayMeals.filter { it.mealType.equals("Snack", true) }.sumOf { it.calories }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MealsUiState())

    fun addMeal(
        name: String, calories: Int, protein: Int, carbs: Int, fat: Int,
        mealType: String, cuisine: String, goalType: String
    ) {
        viewModelScope.launch {
            repo.addMeal(name, calories, protein, carbs, fat, mealType, cuisine, goalType)
        }
    }

    fun deleteMeal(meal: MealLog) { viewModelScope.launch { repo.deleteMeal(meal) } }
}

class MealsViewModelFactory(private val repo: MealRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST") return MealsViewModel(repo) as T
    }
}
