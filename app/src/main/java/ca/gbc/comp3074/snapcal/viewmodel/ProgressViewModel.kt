package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.db.DayTotal
import ca.gbc.comp3074.snapcal.data.repo.MealRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class ProgressViewModel(private val repo: MealRepository) : ViewModel() {

    fun lastNDaysCalories(n: Int): StateFlow<List<DayTotal>> {
        val sinceMillis = System.currentTimeMillis() - (n.toLong() * 24 * 60 * 60 * 1000)
        return repo.observeCaloriesByDay(sinceMillis)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }
}
