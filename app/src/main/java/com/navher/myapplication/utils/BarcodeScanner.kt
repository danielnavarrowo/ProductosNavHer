package com.navher.myapplication.utils

import androidx.navigation.NavController

// Convertir de object a clase regular para evitar fugas de memoria
class BarcodeScanner(private val navController: NavController) {
    private var onScanCallback: ((String) -> Unit)? = null

    fun startScan(onQueryChange: (String) -> Unit) {
        onScanCallback = onQueryChange
        navController.navigate("barcode_scanner")
    }

    fun onBarcodeScanned(barcode: String) {
        onScanCallback?.invoke(barcode)
        navController.popBackStack()
    }

    fun cancelScan() {
        navController.popBackStack()
    }

}