package ca.gbc.comp3074.snapcal.ui.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository
import kotlinx.coroutines.launch

class WaterViewModel(private val repo: WaterRepository) : ViewModel() {

    val todayTotalMl = repo.todayTotalMl
    val entries = repo.entries   // ✅ add this if WaterScreen shows history

    fun add(amountMl: Int) {
        if (amountMl <= 0) return
        viewModelScope.launch { repo.add(amountMl) }
    }

    fun subtract(amountMl: Int) {
        if (amountMl <= 0) return
        viewModelScope.launch { repo.subtract(amountMl) }
    }
}
