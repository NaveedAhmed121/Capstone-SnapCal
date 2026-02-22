package ca.gbc.comp3074.snapcal.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.gbc.comp3074.snapcal.data.repo.MealRepository
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository

class MealsViewModelFactory(private val repo: MealRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MealsViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class ProgressViewModelFactory(private val mealRepo: MealRepository, private val waterRepo: WaterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProgressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProgressViewModel(mealRepo, waterRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
