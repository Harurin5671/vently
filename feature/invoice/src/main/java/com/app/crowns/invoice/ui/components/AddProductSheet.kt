package com.app.crowns.invoice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnPrimary
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import com.app.crowns.invoice.domain.model.InvoiceItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductSheet(
    products: List<Product>,
    categories: List<Category>,
    searchQuery: String,
    selectedCategory: String?,
    onSearchChanged: (String) -> Unit,
    onCategoryChanged: (String?) -> Unit,
    onAddItem: (InvoiceItem) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var expandedProductId by remember { mutableStateOf<String?>(null) }
    var expandedQuantity by remember { mutableIntStateOf(1) }

    val filtered = products.filter { product ->
        (searchQuery.isBlank() || product.name.contains(searchQuery, ignoreCase = true) ||
                (product.sku?.contains(searchQuery, ignoreCase = true) ?: false)) &&
                (selectedCategory == null || product.category?.name == selectedCategory)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceContainerLowest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Agregar producto",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = OnSurface,
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Cerrar",
                        tint = OnSurfaceVariant,
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Search
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Buscar por nombre o SKU...", color = Outline) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = Outline,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = OutlineVariant,
                ),
            )

            Spacer(Modifier.height(12.dp))

            // Category filters
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    CategoryChip(
                        label = "Todos",
                        isSelected = selectedCategory == null,
                        onClick = { onCategoryChanged(null) },
                    )
                }
                items(categories) { category ->
                    CategoryChip(
                        label = category.name,
                        isSelected = selectedCategory == category.name,
                        onClick = { onCategoryChanged(category.name) },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Product list
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(400.dp),
            ) {
                items(filtered, key = { it.id ?: "" }) { product ->
                    val isExpanded = expandedProductId == product.id
                    val isOutOfStock = product.currentStock == 0

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(SurfaceContainerLowest)
                            .border(
                                width = if (isExpanded) 2.dp else 1.dp,
                                color = when {
                                    isExpanded -> Primary
                                    product.currentStock in 1..3 -> Color(0xFFF59E0B)
                                    else -> OutlineVariant
                                },
                                shape = RoundedCornerShape(12.dp),
                            )
                            .then(
                                if (!isOutOfStock)
                                    Modifier.clickable {
                                        if (isExpanded) {
                                            expandedProductId = null
                                            expandedQuantity = 1
                                        } else {
                                            expandedProductId = product.id
                                            expandedQuantity = 1
                                        }
                                    }
                                else Modifier
                            ),
                    ) {
                        // Product row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            // Icon placeholder
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isOutOfStock) SurfaceContainerHigh
                                        else PrimaryContainer.copy(alpha = 0.12f)
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = product.name.first().uppercase(),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOutOfStock) Outline else Primary,
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOutOfStock) OnSurfaceVariant else OnSurface,
                                )
                                Text(
                                    text = "SKU: ${product.sku ?: "S/SKU"}",
                                    fontSize = 12.sp,
                                    color = Outline,
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                val locale = LocalConfiguration.current.locales[0]
                                Text(
                                    text = "S/ ${String.format(locale, "%.2f", product.salePrice)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isOutOfStock) OnSurfaceVariant else Primary,
                                )
                                // Stock badge
                                val (badgeText, badgeBg, badgeFg) = when {
                                    product.currentStock == 0 -> Triple("SIN STOCK", Color(0xFFF1F5F9), Color(0xFF64748B))
                                    product.currentStock <= 3 -> Triple("BAJO STOCK (${product.currentStock})", Color(0xFFFFFBEB), Color(0xFFF59E0B))
                                    else -> Triple("STOCK (${product.currentStock})", Color(0xFFECFDF5), Color(0xFF10B981))
                                }
                                Text(
                                    text = badgeText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeFg,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(999.dp))
                                        .background(badgeBg)
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                )
                            }
                        }

                        // Expanded area
                        if (isExpanded) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PrimaryContainer.copy(alpha = 0.05f))
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    Text(
                                        text = "Cantidad",
                                        fontSize = 14.sp,
                                        color = OnSurfaceVariant,
                                    )
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(SurfaceContainerHigh)
                                            .padding(4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    ) {
                                        IconButton(
                                            onClick = { if (expandedQuantity > 1) expandedQuantity-- },
                                            modifier = Modifier.size(32.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Remove,
                                                contentDescription = null,
                                                tint = Primary,
                                            )
                                        }
                                        Text(
                                            text = "$expandedQuantity",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = OnSurface,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.width(24.dp),
                                        )
                                        IconButton(
                                            onClick = { expandedQuantity++ },
                                            modifier = Modifier.size(32.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = null,
                                                tint = OnPrimary,
                                                modifier = Modifier
                                                    .background(Primary, RoundedCornerShape(4.dp))
                                                    .padding(4.dp),
                                            )
                                        }
                                    }
                                }

                                Button(
                                    onClick = {
                                        onAddItem(
                                            InvoiceItem(
                                                productId = product.id ?: "",
                                                name = product.name,
                                                sku = product.sku,
                                                unitPrice = product.salePrice,
                                                quantity = expandedQuantity,
                                            )
                                        )
                                        expandedProductId = null
                                        expandedQuantity = 1
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Primary,
                                        contentColor = OnPrimary,
                                    ),
                                ) {
                                    Text(
                                        text = "Agregar al comprobante",
                                        fontWeight = FontWeight.SemiBold,
                                    )
                                }
                            }
                        }
                    }
                }

                item { Spacer(Modifier.height(16.dp)) }
            }
        }
    }
}

@Composable
private fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = if (isSelected) OnPrimary else OnSurfaceVariant,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (isSelected) Primary else SurfaceContainerHigh)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
    )
}