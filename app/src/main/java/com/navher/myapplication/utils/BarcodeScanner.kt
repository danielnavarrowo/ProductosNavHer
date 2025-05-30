package com.navher.myapplication.utils
import android.content.Context
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning


object BarcodeScanner {
    private val options = GmsBarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODABAR,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_ITF,
        )
        .build()

    private lateinit var scanner: GmsBarcodeScanner
    var initialized = false

    fun initialize(context: Context) {
        scanner = GmsBarcodeScanning.getClient(context, options)
        initialized = true
    }

    fun startScan(onQueryChange: (String) -> Unit) {
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                val rawValue: String? = barcode.rawValue?.trimStart('0')
                onQueryChange(rawValue ?: "")
            }
    }
}