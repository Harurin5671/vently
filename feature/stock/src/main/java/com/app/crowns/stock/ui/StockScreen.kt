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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.StockLow
import com.app.crowns.designsystem.color.StockLowBg
import com.app.crowns.designsystem.color.StockNone
import com.app.crowns.designsystem.color.StockNoneBg
import com.app.crowns.designsystem.color.Surface
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.designsystem.preview.ThemePreviews
import com.app.crowns.designsystem.theme.VentlyTheme
import com.app.crowns.domain.product.model.Product
import com.app.crowns.ui.components.shimmerEffect

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
        if (state.products.isEmpty() && !state.isLoading) {
            EmptyStockState(
                onAddProduct = { onEvent(StockEvent.OnAddProduct) },
                modifier = Modifier.padding(innerPadding)
            )
        } else {
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
                            onClick = { onEvent(StockEvent.OnProductClick(product.id ?: "")) },
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
}

@Composable
private fun ProductItemSkeleton() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .shimmerEffect()
        )

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 140.dp, height = 16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 12.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(width = 60.dp, height = 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
            Box(
                modifier = Modifier
                    .size(width = 40.dp, height = 14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .shimmerEffect()
            )
        }
    }
}

@Composable
private fun EmptyStockState(
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Inventory,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = OutlineVariant
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Tu inventario está vacío",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Comienza agregando tu primer producto para llevar el control de tu stock.",
            fontSize = 14.sp,
            color = OnSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAddProduct,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Agregar producto")
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

//@Composable
//private fun ProductItem(
//    product: Product,
//    onClick: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val statusColor = when (product.stockStatus) {
//        StockStatus.LOW -> StockLow
//        StockStatus.NONE -> StockNone
//        StockStatus.OK -> null
//    }
//    val statusLabel = when (product.stockStatus) {
//        StockStatus.LOW -> "STOCK BAJO"
//        StockStatus.NONE -> "SIN STOCK"
//        StockStatus.OK -> null
//    }
//
//    Row(
//        modifier = modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(12.dp))
//            .background(SurfaceContainerLowest)
//            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
//            .then(
//                if (statusColor != null)
//                    Modifier.border(
//                        width = 1.dp,
//                        color = OutlineVariant,
//                        shape = RoundedCornerShape(12.dp),
//                    )
//                else Modifier
//            )
//            .clickable { onClick() }
//            .padding(16.dp),
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        verticalAlignment = Alignment.CenterVertically,
//    ) {
//        // Color dot
//        Box(
//            modifier = Modifier
//                .size(12.dp)
//                .clip(CircleShape)
//                .background(
//                    try {
//                        Color(product.colorHex.toColorInt())
//                    } catch (_: Exception) {
//                        Primary
//                    }
//                ),
//        )
//
//        // Info
//        Column(modifier = Modifier.weight(1f)) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//            ) {
//                Text(
//                    text = product.name,
//                    fontSize = 15.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = OnSurface,
//                )
//                if (statusLabel != null && statusColor != null) {
//                    Text(
//                        text = statusLabel,
//                        fontSize = 10.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = statusColor,
//                        modifier = Modifier
//                            .clip(RoundedCornerShape(999.dp))
//                            .background(statusColor.copy(alpha = 0.12f))
//                            .padding(horizontal = 8.dp, vertical = 2.dp),
//                    )
//                }
//            }
//            Text(
//                text = "SKU: ${product.sku}",
//                fontSize = 12.sp,
//                color = Outline,
//            )
//            Spacer(Modifier.height(4.dp))
//            Text(
//                text = product.category,
//                fontSize = 11.sp,
//                color = OnSurfaceVariant,
//                modifier = Modifier
//                    .clip(RoundedCornerShape(6.dp))
//                    .background(SurfaceContainerHigh)
//                    .padding(horizontal = 8.dp, vertical = 2.dp),
//            )
//        }
//
//        // Quantity + price
//        Column(horizontalAlignment = Alignment.End) {
//            Text(
//                text = "${product.quantity} uds",
//                fontSize = 16.sp,
//                fontWeight = FontWeight.Bold,
//                color = if (product.stockStatus == StockStatus.NONE) StockNone else Primary,
//            )
//            Text(
//                text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", product.price)}",
//                fontSize = 14.sp,
//                fontWeight = FontWeight.Medium,
//                color = OnSurfaceVariant,
//            )
//        }
//    }
//}

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
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Imagen o inicial
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryContainer.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center,
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
//                    contentScale = ContentScale.Crop,
                )
            } else {
                Text(
                    text = product.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                )
            }
        }

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
                text = "SKU: ${product.sku ?: "S/SKU"}",
                fontSize = 12.sp,
                color = Outline,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = product.category?.name ?: "Sin categoría",
                fontSize = 11.sp,
                color = OnSurfaceVariant,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(SurfaceContainerHigh)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }

        // Stock + precio
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${product.currentStock} uds",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (product.stockStatus == StockStatus.NONE) StockNone else Primary,
            )
            Text(
                text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", product.salePrice)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant,
            )
        }
    }
}

//@ThemePreviews
//@Composable
//private fun StockScreenPreview() {
//    val mockProducts = listOf(
//        Product("1", "Agua Mineral 500ml", "BEB-0012", "Bebidas", 5, 2.50, "#3B82F6"),
//        Product("2", "Polo Algodón M", "ROP-0941", "Ropa", 0, 45.00, "#10B981"),
//        Product("3", "Gaseosa Inka 1.5L", "BEB-0412", "Bebidas", 24, 7.50, "#3B82F6"),
//        Product("4", "Galletas Soda Pack", "SNA-0052", "Snacks", 48, 1.20, "#F59E0B"),
//        Product("5", "Jean Slim Fit 32", "ROP-0231", "Ropa", 12, 89.90, "#10B981"),
//    )
//    VentlyTheme {
//        StockContent(
//            state = StockUiState(
//                products = mockProducts,
//                filteredProducts = mockProducts,
//                totalProducts = mockProducts.size,
//                lowStockCount = mockProducts.count { it.stockStatus == StockStatus.LOW },
//                noStockCount = mockProducts.count { it.stockStatus == StockStatus.NONE },
//            ),
//            onEvent = {}
//        )
//    }
//}
