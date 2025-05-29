package com.navher.myapplication.ui.screens

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.foundation.background
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
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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

    val pullToRefreshState = rememberPullToRefreshState()
    val onRefreshLambda = { productsViewModel.loadProducts() }

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondary)
            .pullToRefresh(
                state = pullToRefreshState,
                isRefreshing = isLoading,
                onRefresh = onRefreshLambda
            ),
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
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = .1f),
                        )
                        .padding(start = 12.dp, top = 16.dp, end = 12.dp)
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
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredProducts, key = { it.codigo }) { product ->
                                ProductCard(
                                    product = product,
                                    forceExpanded = shouldAutoExpand
                                )
                            }
                        }
                    } else if (isLoading && productsList.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            ContainedLoadingIndicator(
                                modifier = Modifier
                                    .size(162.dp)
                            )
                        }
                    }
                }

                val scaleFraction = if (isLoading) 1f else LinearOutSlowInEasing.transform(pullToRefreshState.distanceFraction).coerceIn(0f, 1f)
                Box(
                    Modifier
                        .align(Alignment.TopCenter)
                        .graphicsLayer {
                            scaleX = scaleFraction
                            scaleY = scaleFraction
                        }
                ) {
                    PullToRefreshDefaults.LoadingIndicator(state = pullToRefreshState, isRefreshing = isLoading)
                }
            }
        }
    )
}
