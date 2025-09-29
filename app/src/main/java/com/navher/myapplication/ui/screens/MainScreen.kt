package com.navher.myapplication.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.navher.myapplication.R
import com.navher.myapplication.ui.components.LastUpdate
import com.navher.myapplication.ui.components.ProductCard
import com.navher.myapplication.ui.components.ScannerButton
import com.navher.myapplication.ui.components.SearchBar
import com.navher.myapplication.viewmodels.ProductsViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
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

    val filteredProducts = remember(searchQuery, productsList) {
        val sortedList = productsList.sortedByDescending { it.iprioridad == 1 }
        if (searchQuery.isEmpty()) {
            sortedList
        } else {
            sortedList.filter {
                it.descripcion.contains(searchQuery, ignoreCase = true) ||
                        it.codigo.contains(searchQuery, ignoreCase = true)
            }

        }
    }

    val shouldAutoExpand = remember(filteredProducts) {
        filteredProducts.size <= 3
    }

    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = .13f),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = .05f),
                        shape = MaterialTheme.shapes.large
                    )
                    .statusBarsPadding()
                    .padding(start = 12.dp, end = 12.dp, bottom = 12.dp),
            ) {
                Row(
                    modifier = Modifier
                        .requiredHeight(56.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                Spacer(modifier = Modifier.size(16.dp))
                LastUpdate(updateDate, navController)
            }
        },
        content = { innerPadding ->
            PullToRefreshBox(
                isRefreshing = isLoading,
                state = pullToRefreshState,
                onRefresh = { productsViewModel.loadProducts() },
                modifier = Modifier.padding(innerPadding),
                indicator = {
                    PullToRefreshDefaults.LoadingIndicator(
                        state = pullToRefreshState,
                        isRefreshing = isLoading,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 12.dp, end = 12.dp)
                        .fillMaxSize(),
                ) {
                    if (filteredProducts.isEmpty() && !isLoading) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.error_48px),
                                contentDescription = "No se encontró el producto.",
                                tint = MaterialTheme.colorScheme.surfaceTint,
                                modifier = Modifier
                                    .size(96.dp)
                            )
                            Spacer(modifier = Modifier.size(25.dp))
                            Text(
                                text = "No se encontró el producto.",
                                style = MaterialTheme.typography.displayLarge,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    } else if (filteredProducts.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            item {
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            items(filteredProducts.take(50), key = { it.codigo }) { product ->
                                val index = filteredProducts.indexOf(product)
                                val displayedProducts = filteredProducts.take(50)
                                ProductCard(
                                    product = product,
                                    forceExpanded = shouldAutoExpand,
                                    isFirstItem = index == 0,
                                    isLastItem = index == displayedProducts.size - 1
                                )
                            }

                            item {
                                Spacer(modifier = Modifier.navigationBarsPadding())
                            }

                        }
                    }
                }
            }
        }
    )
}
