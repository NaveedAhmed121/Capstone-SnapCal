package ca.gbc.comp3074.snapcal.ui.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository

class WaterViewModelFactory(private val repo: WaterRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WaterViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
