package ca.gbc.comp3074.snapcal.ui.screens
import androidx.compose.runtime.*
import androidx.lifecycle.*
import ca.gbc.comp3074.snapcal.data.remote.OpenFoodFactsService
import ca.gbc.comp3074.snapcal.data.remote.Product
import kotlinx.coroutines.launch

class ScanViewModel : ViewModel() {
    private val foodFactsService = OpenFoodFactsService()
    var product by mutableStateOf<Product?>(null)
        private set
    var isLoading by mutableStateOf(false)
        private set

    fun fetchProduct(barcode: String) {
        viewModelScope.launch {
            isLoading = true
            product = foodFactsService.getProduct(barcode)?.product
            isLoading = false
        }
    }

    fun clearProduct() {
        product = null
        isLoading = false
    }
}
