package ca.gbc.comp3074.snapcal.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import ca.gbc.comp3074.snapcal.data.db.DatabaseProvider
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WaterViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = WaterRepository(
        DatabaseProvider.get(app).waterDao()
    )

    val todayTotalMl = repo.todayTotalMl.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    val entries = repo.entries.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun add(amountMl: Int) {
        viewModelScope.launch {
            repo.add(amountMl)
        }
    }
}
