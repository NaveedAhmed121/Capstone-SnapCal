package ca.gbc.comp3074.snapcal.ui.water
import androidx.lifecycle.*
import ca.gbc.comp3074.snapcal.data.repo.WaterRepository
import kotlinx.coroutines.launch
class WaterViewModel(private val repo: WaterRepository) : ViewModel() {
    val todayTotalMl = repo.todayTotalMl; val entries = repo.entries
    fun add(amountMl:Int) { if(amountMl>0) viewModelScope.launch{repo.add(amountMl)} }
    fun subtract(amountMl:Int) { if(amountMl>0) viewModelScope.launch{repo.subtract(amountMl)} }
}
class WaterViewModelFactory(private val repo:WaterRepository) : ViewModelProvider.Factory {
    override fun <T:ViewModel> create(modelClass:Class<T>): T { @Suppress("UNCHECKED_CAST") return WaterViewModel(repo) as T }
}
