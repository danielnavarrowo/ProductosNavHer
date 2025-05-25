package com.navher.myapplication.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.navher.myapplication.ui.components.LastUpdate
import com.navher.myapplication.ui.components.ProductCard
import com.navher.myapplication.ui.components.ScannerButton
import com.navher.myapplication.ui.components.SearchBar
import com.navher.myapplication.viewmodels.ProductsViewModel

@Composable
fun MainScreen(
    productsViewModel: ProductsViewModel,
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    navController: NavController
) {
    val productsList by productsViewModel.products.collectAsState()
    val isLoading by productsViewModel.isLoading.collectAsState()
    val updateDate by productsViewModel.updateDate.collectAsState()


    // Loading indicator
    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val filteredProducts = remember(searchQuery, productsList) {
        if (searchQuery.isEmpty()) productsList
        else {
            productsList.filter {
                it.descripcion.contains(searchQuery, ignoreCase = true) ||
                        it.codigo.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val shouldAutoExpand = remember(filteredProducts) {
        filteredProducts.size <= 3
    }

    Scaffold (
        topBar = {
            Column (
                modifier = Modifier
                .statusBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .requiredHeight(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange,
                        modifier = Modifier
                            .weight(.84f)
                            .fillMaxHeight()
                    )
                    ScannerButton(onQueryChange)
                }
                LastUpdate(updateDate, navController)
            }

        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { productsViewModel.loadProducts() },
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ) {
                Icon(Icons.Filled.Refresh, "Refrescar datos")
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).padding(horizontal = 12.dp),
            ) {
                if (filteredProducts.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Info,
                            contentDescription = "No se encontró el producto.",
                            tint = MaterialTheme.colorScheme.surfaceTint,
                            modifier = Modifier
                                .size(96.dp)
                        )
                        Spacer(modifier = Modifier.size(25.dp))
                        Text(
                            text = "No se encontró el producto.",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontWeight = FontWeight.Black,
                            ),
                            color = MaterialTheme.colorScheme.onBackground,
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        items(filteredProducts.take(50)) { product ->
                            ProductCard(
                                product,
                                forceExpanded = shouldAutoExpand
                            )
                        }
                    }
                }
            }
        }
    )
}