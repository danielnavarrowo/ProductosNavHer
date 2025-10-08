package com.navher.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.navher.myapplication.ui.screens.LoginScreen
import com.navher.myapplication.ui.screens.MainScreen
import com.navher.myapplication.ui.screens.SettingsScreen
import com.navher.myapplication.ui.theme.MyApplicationTheme
import com.navher.myapplication.utils.BarcodeScanner
import com.navher.myapplication.utils.BarcodeScanner.startScan
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.utils.ModuleInstallManager
import com.navher.myapplication.utils.ModuleInstallManager.moduleInstallClient
import com.navher.myapplication.utils.ModuleInstallManager.moduleInstallRequest
import com.navher.myapplication.viewmodels.AuthViewModel
import com.navher.myapplication.viewmodels.AuthViewModelFactory
import com.navher.myapplication.viewmodels.ProductsViewModel
import com.navher.myapplication.viewmodels.ProductsViewModelFactory


class MainActivity : ComponentActivity() {

    private lateinit var dataService: DataService
    private lateinit var productsViewModel: ProductsViewModel
    private var searchQuery by mutableStateOf("")
    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DataStore
        dataService = DataService(this)
        val factory = ProductsViewModelFactory(dataService)
        productsViewModel = ViewModelProvider(this, factory)[ProductsViewModel::class.java]
        val authFactory = AuthViewModelFactory(dataService)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]
        ModuleInstallManager.initialize(this)
        BarcodeScanner.initialize(this)
        moduleInstallClient.installModules(moduleInstallRequest)
        handleIntent(intent)
        enableEdgeToEdge()
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
        var startDestination by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            authViewModel.checkSession {
                startDestination = "main"
                println("User is logged in, from main activity")
                productsViewModel.loadProducts()
            }
            if (startDestination == null) {
                println("User is not logged in, from main activity")
                startDestination = "login"
            }
        }

        startDestination?.let { start ->
            NavHost(navController = navController, startDestination = start) {
                composable("main") {
                    MainScreen(
                        productsViewModel = productsViewModel,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        navController = navController
                    )
                }
                composable("settings") { SettingsScreen(navController) }
                composable("login") {
                    LoginScreen(
                        authViewModel = authViewModel,
                        onLoginSuccess = {
                            productsViewModel.loadProducts()
                            navController.navigate("main") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    }

}
