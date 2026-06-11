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
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.formattedId
import androidx.core.graphics.toColorInt

@Composable
fun HistoryInvoiceItem(
    invoice: InvoiceEntity,
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
        // Info del estado desde la BD
        val statusColor = invoice.statusInfo?.color?.let {
            try { Color(it.toColorInt()) } catch (e: Exception) { null }
        } ?: TextMuted

        // Avatar: Usamos el color de la BD con transparencia para el fondo
        val isBoleta = invoice.documentType == "03"
        val avatarBg = statusColor.copy(alpha = 0.1f)
        val avatarTextColor = statusColor

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(avatarBg),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isBoleta) "B" else "F",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = avatarTextColor,
            )
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invoice.formattedId,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Text(
                text = invoice.customerLegalName,
                fontSize = 12.sp,
                color = Outline,
            )
        }

        // Amount + status
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "S/ ${String.format(LocalLocale.current.platformLocale, "%,.2f", invoice.total.toDoubleOrNull() ?: 0.0)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            
            val statusLabel = invoice.statusInfo?.name ?: invoice.status.uppercase()

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
}