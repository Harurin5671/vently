package com.app.crowns.home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.PrimaryContainer
import com.app.crowns.designsystem.color.Secondary
import com.app.crowns.designsystem.color.SecondaryContainer
import com.app.crowns.designsystem.color.Success
import com.app.crowns.designsystem.color.TextMuted
import com.app.crowns.home.ui.dashboard.InvoiceStatus
import com.app.crowns.home.ui.dashboard.InvoiceType
import com.app.crowns.home.ui.dashboard.RecentInvoice

@Composable
fun RecentInvoiceItem(
    invoice: RecentInvoice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val locale = LocalConfiguration.current.locales[0]

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar con letra del tipo
        val isBoleta = invoice.type == InvoiceType.BOLETA
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    if (isBoleta) SecondaryContainer.copy(alpha = 0.2f)
                    else PrimaryContainer.copy(alpha = 0.2f)
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isBoleta) "B" else "F",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isBoleta) Secondary else Primary,
            )
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invoice.number,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Text(
                text = invoice.clientName,
                fontSize = 12.sp,
                color = TextMuted,
            )
        }

        // Amount + status
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "S/ ${String.format(locale, "%.2f", invoice.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val (statusLabel, statusColor) = when (invoice.status) {
                    InvoiceStatus.ACCEPTED -> "ACEPTADO" to Success
                    InvoiceStatus.PENDING -> "PENDIENTE" to androidx.compose.ui.graphics.Color(
                        0xFFF59E0B
                    )

                    InvoiceStatus.REJECTED -> "RECHAZADO" to androidx.compose.ui.graphics.Color(
                        0xFFEF4444
                    )

                    InvoiceStatus.CANCELLED -> "ANULADO" to OutlineVariant
                }
                StatusBadge(label = statusLabel, color = statusColor)
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = OutlineVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
    }
}