package com.navher.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.navher.myapplication.ui.screens.MainScreen
import com.navher.myapplication.ui.screens.SettingsScreen
import com.navher.myapplication.ui.theme.MyApplicationTheme
import com.navher.myapplication.utils.BarcodeScanner
import com.navher.myapplication.utils.BarcodeScanner.startScan
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.utils.ModuleInstallManager
import com.navher.myapplication.utils.ModuleInstallManager.moduleInstallClient
import com.navher.myapplication.utils.ModuleInstallManager.moduleInstallRequest
import com.navher.myapplication.viewmodels.ProductsViewModel
import com.navher.myapplication.viewmodels.ProductsViewModelFactory


class MainActivity : ComponentActivity() {

    private lateinit var dataService: DataService
    private lateinit var productsViewModel: ProductsViewModel
    private var searchQuery by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize DataStore
        dataService = DataService(this)
        val factory = ProductsViewModelFactory(dataService)
        productsViewModel = ViewModelProvider(this, factory)[ProductsViewModel::class.java]
        ModuleInstallManager.initialize(this)
        BarcodeScanner.initialize(this)
        moduleInstallClient.installModules(moduleInstallRequest)

        handleIntent(intent)

        setContent {
            MyApplicationTheme {
                MyApp()
            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle the new intent (e.g., when the activity is already running)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Check if we need to start the scanner (from QS Tile or any other source)
        if (intent.getBooleanExtra("START_SCANNER", false))
            // Start the scanner directly
            startScan(onQueryChange = { searchQuery = it })

    }

    @Composable
    fun MyApp() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "main") {
            composable("main") {
                MainScreen(
                    productsViewModel = productsViewModel,
                    dataService = dataService,
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it },
                    navController = navController
                )
            }
            composable("settings") { SettingsScreen(navController) }
        }
    }

}
