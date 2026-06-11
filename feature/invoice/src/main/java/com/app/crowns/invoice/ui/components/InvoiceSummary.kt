package com.app.crowns.invoice.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import androidx.compose.ui.platform.LocalLocale

@Composable
fun InvoiceSummary(
    subtotal: Double,
    igv: Double,
    total: Double,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        SummaryRow(
            label = "Subtotal",
            value = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", subtotal)}",
        )
        SummaryRow(
            label = "IGV (18%)",
            value = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", igv)}",
        )
        HorizontalDivider(color = OutlineVariant, thickness = 0.5.dp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "Total",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurface,
            )
            Text(
                text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", total)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Primary,
            )
        }
    }
}