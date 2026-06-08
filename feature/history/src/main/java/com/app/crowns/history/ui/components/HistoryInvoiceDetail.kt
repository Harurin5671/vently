package com.app.crowns.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.ui.components.InvoiceActionButtons

@Composable
fun HistoryInvoiceDetail(
    invoice: InvoiceEntity,
    onDownloadPdf: () -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        HorizontalDivider(
            color = OutlineVariant,
            thickness = 0.5.dp,
            modifier = Modifier.padding(bottom = 4.dp),
        )

        // Date + RUC grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "FECHA Y HORA",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Outline,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text = formatDateTime(invoice.issueDate),
                    fontSize = 14.sp,
                    color = OnSurface,
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "CLIENTE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Outline,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text = invoice.customerDocNumber,
                    fontSize = 14.sp,
                    color = OnSurface,
                )
            }
        }
        
        Text(
            text = invoice.customerLegalName,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = OnSurface,
            modifier = Modifier.fillMaxWidth()
        )

        // Items
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "PRODUCTOS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = Outline,
                letterSpacing = 0.5.sp,
            )
            invoice.items.forEach { item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .border(
                            width = 0.5.dp,
                            color = OutlineVariant.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(0.dp),
                        )
                        .padding(bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "${item.quantity}x ${item.description}",
                        fontSize = 14.sp,
                        color = OnSurface,
                    )
                    Text(
                        text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", item.total.toDoubleOrNull() ?: 0.0)}",
                        fontSize = 14.sp,
                        color = OnSurface,
                    )
                }
            }
        }

        // Breakdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceContainerHigh)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            BreakdownRow("Subtotal", invoice.totalGravada.toDoubleOrNull() ?: 0.0)
            BreakdownRow("IGV (18%)", invoice.totalIgv.toDoubleOrNull() ?: 0.0)
            HorizontalDivider(
                color = OutlineVariant.copy(alpha = 0.3f),
                thickness = 0.5.dp,
                modifier = Modifier.padding(vertical = 4.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "Total",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                )
                Text(
                    text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", invoice.total.toDoubleOrNull() ?: 0.0)}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = OnSurface,
                )
            }
        }

        // Actions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            InvoiceActionButtons(
                onDownloadPdf = onDownloadPdf,
                onSend = onSend,
            )
            TextButton(
                onClick = onCancel,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = "Anular Documento",
                    color = Color(0xFFBA1A1A),
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun BreakdownRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, fontSize = 12.sp, color = Outline)
        Text(
            text = "S/ ${String.format(LocalLocale.current.platformLocale, "%.2f", amount)}",
            fontSize = 12.sp,
            color = Outline,
        )
    }
}

private fun formatDateTime(dateTime: String): String {
    return try {
        val date = dateTime.take(10)
        val time = dateTime.substring(11, 16)
        val parts = date.split("-")
        "${parts[2]} ${monthName(parts[1].toInt())} ${parts[0]}, $time"
    } catch (_: Exception) {
        dateTime
    }
}

private fun monthName(month: Int): String = when (month) {
    1 -> "Ene"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Abr"
    5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Ago"
    9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dic"
    else -> ""
}
