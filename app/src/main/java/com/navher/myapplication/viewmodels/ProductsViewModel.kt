package com.navher.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.utils.Products
import com.navher.myapplication.utils.formatDateToSpanish
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(private val dataService: DataService) : ViewModel() {
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _updateDate = MutableStateFlow("")
    val updateDate: StateFlow<String> = _updateDate.asStateFlow()

    fun loadProducts() {
        viewModelScope.launch {
            try {
                // Load cached data immediately
                val cachedProducts = dataService.getCachedData()
                val hasCachedData = cachedProducts.isNotEmpty()

                if (hasCachedData) {
                    _products.value = cachedProducts
                    _updateDate.value = formatDateToSpanish(dataService.getCachedUpdateDate())
                    _isLoading.value = false
                } else {
                    // No cached data, keep loading indicator active
                    _isLoading.value = true
                }

                // Check for updates in the background
                val updatedProducts = dataService.checkAndFetchUpdates()
                if (updatedProducts != null) {
                    _products.value = updatedProducts
                    _updateDate.value = formatDateToSpanish(dataService.serverUpdate)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}
