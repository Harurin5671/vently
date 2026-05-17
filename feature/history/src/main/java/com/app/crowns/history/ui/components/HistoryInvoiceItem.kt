package com.app.crowns.history.ui.components

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.OnSurface
import com.app.crowns.designsystem.color.OnSurfaceVariant
import com.app.crowns.designsystem.color.Outline
import com.app.crowns.designsystem.color.Primary
import com.app.crowns.designsystem.color.Success
import com.app.crowns.history.domain.model.HistoryInvoice
import com.app.crowns.history.domain.model.HistoryInvoiceStatus
import com.app.crowns.history.domain.model.HistoryInvoiceType
import java.util.Locale

@Composable
fun HistoryInvoiceItem(
    invoice: HistoryInvoice,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Avatar
        val isBoleta = invoice.type == HistoryInvoiceType.BOLETA
        val avatarBg = when (invoice.status) {
            HistoryInvoiceStatus.CANCELLED -> Color(0xFFF1F5F9)
            else -> if (isBoleta) Color(0xFFF0EDFF) else Color(0xFFE0FAFB)
        }
        val avatarTextColor = when (invoice.status) {
            HistoryInvoiceStatus.CANCELLED -> Outline
            else -> if (isBoleta) Primary else Color(0xFF00C2CB)
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = invoice.type.prefix,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = avatarTextColor,
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
                color = Outline,
            )
        }

        // Amount + status
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "S/ ${String.format(Locale.getDefault(), "%,.2f", invoice.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            val (statusLabel, statusBg, statusColor) = when (invoice.status) {
                HistoryInvoiceStatus.ACCEPTED -> Triple("ACEPTADO", Color(0xFFECFDF5), Color(0xFF10B981))
                HistoryInvoiceStatus.PENDING -> Triple("PENDIENTE", Color(0xFFFFFBEB), Color(0xFFF59E0B))
                HistoryInvoiceStatus.CANCELLED -> Triple("ANULADO", Color(0xFFF1F5F9), Color(0xFF64748B))
                HistoryInvoiceStatus.REJECTED -> Triple("RECHAZADO", Color(0xFFFEF2F2), Color(0xFFEF4444))
            }
            Text(
                text = statusLabel,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(statusBg)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}