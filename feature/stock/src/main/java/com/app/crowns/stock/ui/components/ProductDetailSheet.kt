package com.app.crowns.stock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.model.profitMargin
import com.app.crowns.stock.ui.StockStatus
import com.app.crowns.stock.ui.stockStatus
import com.app.crowns.ui.components.VentlyModalBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailSheet(
    product: Product,
    onDismiss: () -> Unit,
    onEdit: (String) -> Unit,
    unitLabel: String,
    isNavigating: Boolean = false,
) {
    val locale = LocalLocale.current.platformLocale
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

    VentlyModalBottomSheet(
        onDismiss = onDismiss,
        title = product.name,
    ) {
        // Image / avatar
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(PrimaryContainer.copy(alpha = 0.2f))
                .align(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center,
        ) {
            if (product.imageUrl != null) {
                AsyncImage(
                    model = product.imageUrl,
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                Text(
                    text = product.name.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary,
                )
            }
        }

        Spacer(Modifier.height(4.dp))

        // Status badge (centrado)
        if (statusLabel != null && statusColor != null) {
            Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(
                    text = statusLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(statusColor.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider(color = OutlineVariant)
        Spacer(Modifier.height(16.dp))

        // Detail rows
        ProductDetailRow(label = "SKU", value = product.sku ?: "Sin SKU")
        ProductDetailRow(label = "Categoría", value = product.category?.name ?: "Sin categoría")
        ProductDetailRow(label = "Tipo de IGV", value = "${product.igvType.name} (${product.igvType.percentage}%)")
        ProductDetailRow(label = "Unidad de medida", value = unitLabel)
        ProductDetailRow(
            label = "Precio de venta",
            value = "S/ ${String.format(locale, "%.2f", product.salePrice)}",
        )
        ProductDetailRow(
            label = "Precio de costo",
            value = "S/ ${String.format(locale, "%.2f", product.costPrice)}",
        )
        ProductDetailRow(
            label = "Margen de ganancia",
            value = "${String.format(locale, "%.1f", product.profitMargin)}%",
        )
        ProductDetailRow(
            label = "Stock actual",
            value = "${product.currentStock} uds",
            valueColor = statusColor ?: Primary,
        )
        if (product.trackStock && product.minStockAlert != null) {
            ProductDetailRow(label = "Alerta de stock mínimo", value = "${product.minStockAlert} uds")
        }
        val description = product.description
        if (description != null) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Descripción",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurfaceVariant,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = description,
                fontSize = 14.sp,
                color = OnSurface,
            )
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onEdit(product.id ?: "") },
            enabled = !isNavigating,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = OnPrimary,
            ),
        ) {
            if (isNavigating) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = OnPrimary,
                    strokeWidth = 2.dp,
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Editar producto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}