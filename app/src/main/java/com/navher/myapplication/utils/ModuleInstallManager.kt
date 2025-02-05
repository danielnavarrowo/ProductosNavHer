package com.navher.myapplication.utils

import android.content.Context
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallClient
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.tflite.java.TfLite


object ModuleInstallManager {
    lateinit var moduleInstallClient: ModuleInstallClient
    lateinit var moduleInstallRequest: ModuleInstallRequest

    fun initialize(context: Context) {
        moduleInstallClient = ModuleInstall.getClient(context)
        val optionalModuleApi = TfLite.getClient(context)
        moduleInstallRequest = ModuleInstallRequest.newBuilder()
            .addApi(optionalModuleApi)
            .build()
    }
}

