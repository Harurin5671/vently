package com.app.crowns.stock.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.stock.ui.components.*
import com.app.crowns.ui.components.VentlyStockEmptyState
import com.app.crowns.ui.components.shimmerEffect

@Composable
fun StockScreen(
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateToAddProduct: () -> Unit = {},
    onNavigateToEditProduct: (String) -> Unit = {},
    viewModel: StockViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigationEvent.collect { event ->
            when (event) {
                is StockNavigationEvent.NavigateToProductDetail ->
                    onNavigateToProductDetail(event.productId)
                is StockNavigationEvent.NavigateToAddProduct ->
                    onNavigateToAddProduct()
                is StockNavigationEvent.NavigateToEditProduct ->
                    onNavigateToEditProduct(event.productId)
            }
        }
    }

    StockContent(
        state = state,
        onEvent = viewModel::onEvent,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StockContent(
    state: StockUiState,
    onEvent: (StockEvent) -> Unit,
) {
    Scaffold(
        containerColor = Surface,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Mi stock",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = OnSurface,
                    )
                },
                actions = {
                    if (state.products.isNotEmpty() || state.isLoading) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Buscar",
                                tint = OnSurfaceVariant,
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                ),
            )
        }
    ) { innerPadding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading && state.products.isNotEmpty(),
            onRefresh = { onEvent(StockEvent.Refresh) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.products.isEmpty() && !state.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    VentlyStockEmptyState(
                        title = "Tu inventario está vacío",
                        description = "Comienza agregando tu primer producto para llevar el control de tu stock.",
                        icon = Icons.Filled.Inventory,
                        buttonText = "Agregar producto",
                        onButtonClick = { onEvent(StockEvent.OnAddProduct) },
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                // Stats
                item {
                    Spacer(Modifier.height(4.dp))
                    if (state.isLoading && state.products.isEmpty()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(72.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .shimmerEffect()
                                )
                            }
                        }
                    } else {
                        StockStatsRow(
                            total = state.totalProducts,
                            lowStock = state.lowStockCount,
                            noStock = state.noStockCount,
                        )
                    }
                }

                // Filters
                item {
                    if (state.isLoading && state.products.isEmpty()) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .size(width = 80.dp, height = 36.dp)
                                        .clip(RoundedCornerShape(999.dp))
                                        .shimmerEffect()
                                )
                            }
                        }
                    } else {
                        StockFiltersRow(
                            selectedFilter = state.selectedFilter,
                            onFilterSelected = { onEvent(StockEvent.OnFilterSelected(it)) },
                        )
                    }
                }

                // Products
                if (state.isLoading && state.products.isEmpty()) {
                    items(6) {
                        ProductItemSkeleton()
                    }
                } else {
                    items(state.filteredProducts, key = { it.id ?: "" }) { product ->
                        ProductItem(
                            product = product,
                            onClick = { onEvent(StockEvent.OnProductSelected(product)) },
                        )
                    }
                }

                if (state.filteredProducts.isEmpty() && state.searchQuery.isNotEmpty() && !state.isLoading) {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron productos",
                                color = OnSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
    }

    if (state.selectedProduct != null) {
            val unit = state.unitsOfMeasure.find { it.code == state.selectedProduct.unitOfMeasure }
            val unitLabel = if (unit != null) "${unit.code} - ${unit.name}" else state.selectedProduct.unitOfMeasure
        ProductDetailSheet(
            product = state.selectedProduct,
            onDismiss = { onEvent(StockEvent.OnDismissProductDetail) },
            onEdit = { productId -> onEvent(StockEvent.OnEditProduct(productId)) },
            unitLabel = unitLabel,
            isNavigating = state.isNavigatingToEdit,
        )
        }
    }
}