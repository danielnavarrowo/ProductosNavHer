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
import com.navher.myapplication.ui.screens.BarcodeScannerScreen
import com.navher.myapplication.ui.screens.LoginScreen
import com.navher.myapplication.ui.screens.MainScreen
import com.navher.myapplication.ui.screens.SettingsScreen
import com.navher.myapplication.ui.theme.MyApplicationTheme
import com.navher.myapplication.utils.BarcodeScanner
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.viewmodels.AuthViewModel
import com.navher.myapplication.viewmodels.AuthViewModelFactory
import com.navher.myapplication.viewmodels.ProductsViewModel
import com.navher.myapplication.viewmodels.ProductsViewModelFactory


class MainActivity : ComponentActivity() {

    private lateinit var dataService: DataService
    private lateinit var productsViewModel: ProductsViewModel
    private var searchQuery by mutableStateOf("")
    private lateinit var authViewModel: AuthViewModel
    private var shouldStartScanner by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize DataStore
        dataService = DataService(this)
        val factory = ProductsViewModelFactory(dataService)
        productsViewModel = ViewModelProvider(this, factory)[ProductsViewModel::class.java]
        val authFactory = AuthViewModelFactory(dataService)
        authViewModel = ViewModelProvider(this, authFactory)[AuthViewModel::class.java]

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
        setIntent(intent)
        // Handle the new intent (e.g., when the activity is already running)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        // Check if we need to start the scanner (from QS Tile or any other source)
        if (intent.getBooleanExtra("START_SCANNER", false)) {
            shouldStartScanner = true
        }
    }
    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        var startDestination by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(Unit) {
            authViewModel.checkSession {
                startDestination = "main"
                productsViewModel.loadProducts()
            }
            if (startDestination == null) {
                startDestination = "login"
            }
        }

        startDestination?.let { start ->
            NavHost(navController = navController, startDestination = start) {
                composable("main") {
                    // Crear una instancia de BarcodeScanner sin estado estático
                    val barcodeScanner = remember { BarcodeScanner(navController) }

                    // Handle scanner intent from Quick Settings Tile
                    LaunchedEffect(shouldStartScanner) {
                        if (shouldStartScanner) {
                            barcodeScanner.startScan { query ->
                                searchQuery = query
                            }
                            shouldStartScanner = false
                        }
                    }

                    MainScreen(
                        productsViewModel = productsViewModel,
                        searchQuery = searchQuery,
                        onQueryChange = { searchQuery = it },
                        navController = navController,
                        barcodeScanner = barcodeScanner
                    )
                }
                composable("settings") { SettingsScreen(navController) }
                composable("barcode_scanner") {
                    val barcodeScanner = remember { BarcodeScanner(navController) }
                    BarcodeScannerScreen(barcodeScanner)
                }
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
