package com.navher.myapplication
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.navher.myapplication.ui.screens.MainActivityScreen.LastUpdate
import com.navher.myapplication.ui.screens.MainActivityScreen.ProductCard
import com.navher.myapplication.ui.screens.MainActivityScreen.ScannerButton
import com.navher.myapplication.ui.screens.MainActivityScreen.SearchBar
import com.navher.myapplication.ui.theme.MyApplicationTheme
import com.navher.myapplication.utils.BarcodeScanner
import com.navher.myapplication.utils.BarcodeScanner.startScan
import com.navher.myapplication.utils.DataService
import com.navher.myapplication.utils.ModuleInstallManager
import com.navher.myapplication.utils.ModuleInstallManager.moduleInstallClient
import com.navher.myapplication.utils.ModuleInstallManager.moduleInstallRequest
import com.navher.myapplication.utils.Products
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var dataService: DataService
    private var searchQuery by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        // Initialize DataStore
        dataService = DataService(context = this)
        ModuleInstallManager.initialize(this)
        BarcodeScanner.initialize(this)

        // Comprobar si se inició desde el Quick Settings Tile
        if (intent.getBooleanExtra("START_SCANNER", false)) {
            // Iniciar el escáner directamente
            startScan(onQueryChange = { searchQuery = it })
        }

        setContent {
            MyApplicationTheme {
                ProductsList(
                    searchQuery = searchQuery,
                    onQueryChange = { searchQuery = it }
                )
            }
        }
    }


    @Composable
    fun ProductsList(searchQuery: String, onQueryChange: (String) -> Unit) {

        var productsList by remember { mutableStateOf<List<Products>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        val coroutineScope = rememberCoroutineScope()

        // Fetch the product list from DataStore
        LaunchedEffect(Unit) {
            productsList = dataService.getProductsList()
            isLoading = false
        }
        // Loading indicator
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return
        }
        moduleInstallClient.installModules(moduleInstallRequest)

        val filteredProducts = remember(searchQuery, productsList) {
            if (searchQuery.isEmpty()) {
                productsList
            } else {
                productsList.filter {
                    it.descripcion.contains(searchQuery, ignoreCase = true) ||
                            it.codigo.contains(searchQuery, ignoreCase = true)
                }
            }
        }

        val shouldAutoExpand = remember(filteredProducts) {
            filteredProducts.size <= 3
        }

        Box(modifier = Modifier.fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Row (
                    modifier = Modifier.fillMaxWidth().requiredHeight(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = onQueryChange,
                        modifier = Modifier.weight(.84f).fillMaxHeight()
                    )

                    ScannerButton(onQueryChange = onQueryChange)
                }
                                // Shows the last update date
                LastUpdate(dataService)

                if (filteredProducts.isEmpty()) {
                    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Outlined.Info, contentDescription = "No se encontró el producto.", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(96.dp).padding(vertical = 20.dp))
                        Text(text = "No se encontró el producto.", color = MaterialTheme.colorScheme.onSurface, fontSize = 24.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)

                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(filteredProducts.take(50)) { product ->
                            ProductCard(product = product,
                                forceExpanded = shouldAutoExpand)
                        }
                    }
                }
            }
            FloatingActionButton(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            productsList = dataService.getProductsList()
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.align(Alignment.BottomEnd).padding(horizontal = 12.dp, vertical = 30.dp),
                containerColor = MaterialTheme.colorScheme.inversePrimary,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Filled.Refresh, "Floating action button.")
            }
        }
    }

}
