package com.app.crowns.stock.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.crowns.designsystem.color.*
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.designsystem.preview.ThemePreviews
import androidx.compose.ui.platform.LocalLocale
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun StockScreen(
    onNavigateToProductDetail: (String) -> Unit = {},
    onNavigateToAddProduct: () -> Unit = {},
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
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Buscar",
                            tint = OnSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Surface,
                ),
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Stats
            item {
                Spacer(Modifier.height(4.dp))
                StockStatsRow(
                    total = state.totalProducts,
                    lowStock = state.lowStockCount,
                    noStock = state.noStockCount,
                )
            }

            // Filters
            item {
                StockFiltersRow(
                    selectedFilter = state.selectedFilter,
                    onFilterSelected = { onEvent(StockEvent.OnFilterSelected(it)) },
                )
            }

            // Products
            items(state.filteredProducts, key = { it.id }) { product ->
                ProductItem(
                    product = product,
                    onClick = { onEvent(StockEvent.OnProductClick(product.id)) },
                )
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun StockStatsRow(
    total: Int,
    lowStock: Int,
    noStock: Int,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        StockStatCard(
            label = "TOTAL",
            value = "$total",
            valueColor = Primary,
            backgroundColor = SurfaceContainerLowest,
            borderColor = OutlineVariant,
            modifier = Modifier.weight(1f),
        )
        StockStatCard(
            label = "STOCK BAJO",
            value = "$lowStock",
            valueColor = StockLow,
            backgroundColor = StockLowBg,
            borderColor = StockLow.copy(alpha = 0.2f),
            modifier = Modifier.weight(1f),
        )
        StockStatCard(
            label = "SIN STOCK",
            value = "$noStock",
            valueColor = StockNone,
            backgroundColor = StockNoneBg,
            borderColor = StockNone.copy(alpha = 0.2f),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun StockStatCard(
    label: String,
    value: String,
    valueColor: Color,
    backgroundColor: Color,
    borderColor: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = valueColor,
            letterSpacing = 0.8.sp,
        )
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor,
        )
    }
}

@Composable
private fun StockFiltersRow(
    selectedFilter: StockFilter,
    onFilterSelected: (StockFilter) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StockFilter.entries.forEach { filter ->
            val isSelected = filter == selectedFilter
            Text(
                text = filter.label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) OnPrimary else OnSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(
                        if (isSelected) PrimaryContainer
                        else SurfaceContainerHigh
                    )
                    .clickable { onFilterSelected(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            )
        }
    }
}

@Composable
private fun ProductItem(
    product: Product,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val statusColor = when (product.stockStatus) {
        StockStatus.LOW -> StockLow
        StockStatus.NONE -> StockNone
        StockStatus.OK -> null
    }
    val statusLabel = when (product.stockStatus) {
        StockStatus.LOW -> "STOCK BAJO"
        StockStatus.NONE -> "SIN STOCK"
        StockStatus.OK -> null
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .then(
                if (statusColor != null)
                    Modifier.border(
                        width = 1.dp,
                        color = OutlineVariant,
                        shape = RoundedCornerShape(12.dp),
                    )
                else Modifier
            )
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Color dot
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(
                    try {
                        Color(product.colorHex.toColorInt())
                    } catch (e: Exception) {
                        Primary
                    }
                ),
        )

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = product.name,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                )
                if (statusLabel != null && statusColor != null) {
                    Text(
                        text = statusLabel,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(statusColor.copy(alpha = 0.12f))
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                    )
                }
            }
            Text(
                text = "SKU: ${product.sku}",
                fontSize = 12.sp,
                color = Outline,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.category,
                fontSize = 11.sp,
                color = OnSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }

        // Quantity + price
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${product.quantity} uds",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (product.stockStatus == StockStatus.NONE) StockNone else Primary,
            )
            Text(
                text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", product.price)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant,
            )
        }
    }
}

@ThemePreviews
@Composable
private fun StockScreenPreview() {
    val mockProducts = listOf(
        Product("1", "Agua Mineral 500ml", "BEB-0012", "Bebidas", 5, 2.50, "#3B82F6"),
        Product("2", "Polo Algodón M", "ROP-0941", "Ropa", 0, 45.00, "#10B981"),
        Product("3", "Gaseosa Inka 1.5L", "BEB-0412", "Bebidas", 24, 7.50, "#3B82F6"),
        Product("4", "Galletas Soda Pack", "SNA-0052", "Snacks", 48, 1.20, "#F59E0B"),
        Product("5", "Jean Slim Fit 32", "ROP-0231", "Ropa", 12, 89.90, "#10B981"),
    )
    VentlyTheme {
        StockContent(
            state = StockUiState(
                products = mockProducts,
                filteredProducts = mockProducts,
                totalProducts = mockProducts.size,
                lowStockCount = mockProducts.count { it.stockStatus == StockStatus.LOW },
                noStockCount = mockProducts.count { it.stockStatus == StockStatus.NONE },
            ),
            onEvent = {}
        )
    }
}
