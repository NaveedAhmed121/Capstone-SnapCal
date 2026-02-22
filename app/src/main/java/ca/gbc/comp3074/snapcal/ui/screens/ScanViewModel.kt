package ca.gbc.comp3074.snapcal.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
            val response = foodFactsService.getProduct(barcode)
            product = response?.product
            isLoading = false
        }
    }
}