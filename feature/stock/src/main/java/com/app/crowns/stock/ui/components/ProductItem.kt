package com.app.crowns.stock.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.app.crowns.stock.ui.StockStatus
import com.app.crowns.stock.ui.stockStatus

@Composable
fun ProductItem(
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