package com.navher.myapplication.services

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.service.quicksettings.TileService
import com.navher.myapplication.MainActivity
import com.navher.myapplication.utils.BarcodeScanner

class TileService : TileService() {


    @SuppressLint("StartActivityAndCollapseDeprecated")
    override fun onClick() {
        super.onClick()

        // Inicializar el escáner si no está inicializado
        if (!BarcodeScanner.initialized) BarcodeScanner.initialize(this)

        // Crear un intent para iniciar la MainActivity
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            // Indicador especial para saber que fue iniciado desde el tile
            putExtra("FROM_QUICK_SETTINGS", true)
            // Indicador para iniciar el escáner directamente
            putExtra("START_SCANNER", true)
        }

        // Crear un PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Si la app está bloqueada, necesitamos usar unlockAndRun
//        if (isLocked) {
//            unlockAndRun {
//                startActivityAndCollapse(pendingIntent)
//            }
//        } else {
//            startActivityAndCollapse(pendingIntent)
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startActivityAndCollapse(pendingIntent)
        } else {
            startActivityAndCollapse(intent)
        }
    }
}