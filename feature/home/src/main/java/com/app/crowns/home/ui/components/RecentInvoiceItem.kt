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
import com.app.crowns.designsystem.color.*
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity

@Composable
fun RecentInvoiceItem(
    invoice: InvoiceEntity,
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
        // Avatar con letra del tipo y color del estado
        val isBoleta = invoice.documentType == ElectronicDocumentTypeEntity.BOLETA
        val statusColor = invoice.statusInfo?.color?.let {
            try { androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(it)) } catch (e: Exception) { null }
        } ?: TextMuted

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(statusColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (isBoleta) "B" else "F",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = statusColor,
            )
        }

        // Info
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${invoice.serie}-${invoice.correlative.toString().padStart(8, '0')}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Text(
                text = invoice.customerLegalName,
                fontSize = 12.sp,
                color = TextMuted,
            )
        }

        // Amount + status
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "S/ ${String.format(locale, "%,.2f", invoice.total.toDoubleOrNull() ?: 0.0)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = OnSurface,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val statusLabel = invoice.statusInfo?.name ?: invoice.status.uppercase()
                val statusColor = invoice.statusInfo?.color?.let {
                    try {
                        androidx.compose.ui.graphics.Color(android.graphics.Color.parseColor(it))
                    } catch (e: Exception) {
                        null
                    }
                } ?: OutlineVariant

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
