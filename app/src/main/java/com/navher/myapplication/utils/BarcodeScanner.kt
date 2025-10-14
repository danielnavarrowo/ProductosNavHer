package com.navher.myapplication.utils

import androidx.navigation.NavController

object BarcodeScanner {
    private var onScanCallback: ((String) -> Unit)? = null

    fun startScan(navController: NavController, onQueryChange: (String) -> Unit) {
        onScanCallback = onQueryChange
        navController.navigate("barcode_scanner")
    }

    fun onBarcodeScanned(navController: NavController, barcode: String) {
        onScanCallback?.invoke(barcode)
        navController.popBackStack()
    }

    fun cancelScan(navController: NavController) {
        navController.popBackStack()
    }
}