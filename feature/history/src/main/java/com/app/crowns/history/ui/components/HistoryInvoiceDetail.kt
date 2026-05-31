package com.app.crowns.history.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.SurfaceContainerHigh
import com.app.crowns.domain.invoice.entities.InvoiceEntity

@Composable
fun HistoryInvoiceDetail(
    invoice: InvoiceEntity,
    onDownloadPdf: () -> Unit,
    onSend: () -> Unit,
    onCancel: () -> Unit,
    onCopyHash: (String) -> Unit,
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
            // Temporalmente deshabilitado hasta tener cliente en Entity o similar
            if (false) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "RUC / DNI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Outline,
                        letterSpacing = 0.5.sp,
                    )
                    Text(
                        text = "---",
                        fontSize = 14.sp,
                        color = OnSurface,
                    )
                }
            }
        }

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

        // Hash CDR (Usando xmlDocument como placeholder de hash si no hay campo hash explícito en Entity)
        val hashValue = if (invoice.xmlDocument.length > 20) "..." else null
        hashValue?.let { hash ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White)
                    .border(1.dp, OutlineVariant, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(
                        imageVector = Icons.Filled.Verified,
                        contentDescription = null,
                        tint = Outline,
                        modifier = Modifier.size(18.dp),
                    )
                    Text(
                        text = "Hash: $hash",
                        fontSize = 10.sp,
                        color = Outline,
                        maxLines = 1,
                    )
                }
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copiar hash",
                    tint = Primary,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable { onCopyHash(hash) },
                )
            }
        }

        // Actions
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlineActionButton(
                    label = "Descargar PDF",
                    onClick = onDownloadPdf,
                    modifier = Modifier.weight(1f),
                )
                OutlineActionButton(
                    label = "Enviar",
                    onClick = onSend,
                    modifier = Modifier.weight(1f),
                )
            }
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

@Composable
private fun OutlineActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        color = Primary,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Primary, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
    )
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