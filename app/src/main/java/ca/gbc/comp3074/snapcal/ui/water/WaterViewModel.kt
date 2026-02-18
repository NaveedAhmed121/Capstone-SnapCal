package ca.gbc.comp3074.snapcal.ui.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository
import kotlinx.coroutines.launch

class WaterViewModel(private val repo: WaterRepository) : ViewModel() {
    val todayTotalMl = repo.todayTotalMl

    fun add(amountMl: Int) {
        viewModelScope.launch { repo.add(amountMl) }
    }
}
