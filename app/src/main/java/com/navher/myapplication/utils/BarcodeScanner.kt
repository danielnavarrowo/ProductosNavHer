package com.navher.myapplication.utils

import androidx.navigation.NavController

object BarcodeScanner {
    private var navController: NavController? = null
    private var onScanCallback: ((String) -> Unit)? = null

    fun initialize(nav: NavController) {
        navController = nav
    }

    fun startScan(onQueryChange: (String) -> Unit) {
        onScanCallback = onQueryChange
        navController?.navigate("barcode_scanner")
    }

    fun onBarcodeScanned(barcode: String) {
        onScanCallback?.invoke(barcode)
        navController?.popBackStack()
    }

    fun cancelScan() {
        navController?.popBackStack()
    }
}