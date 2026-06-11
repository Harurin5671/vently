package com.app.crowns.stock.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.*

@Composable
fun StockStatsRow(
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