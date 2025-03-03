package com.navher.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.utils.Products
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(private val dataService: DataService) : ViewModel() {
    private val _products = MutableStateFlow<List<Products>>(emptyList())
    val products: StateFlow<List<Products>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadProducts()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _products.value = dataService.getProductsList()
            } finally {
                _isLoading.value = false
            }
        }
    }
}
