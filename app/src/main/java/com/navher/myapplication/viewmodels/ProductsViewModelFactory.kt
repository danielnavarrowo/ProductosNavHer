package com.navher.myapplication.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.navher.myapplication.utils.DataService

class ProductsViewModelFactory(private val dataService: DataService) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductsViewModel(dataService) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }
}