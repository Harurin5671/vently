package com.app.crowns.history.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.crowns.designsystem.color.OutlineVariant
import com.app.crowns.designsystem.color.SurfaceContainerLowest
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.history.ui.HistoryEvent

@Composable
fun HistoryInvoiceCard(
    invoice: InvoiceEntity,
    isExpanded: Boolean,
    onEvent: (HistoryEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceContainerLowest)
            .border(1.dp, OutlineVariant, RoundedCornerShape(12.dp))
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
    ) {
        HistoryInvoiceItem(
            invoice = invoice,
            onClick = { onEvent(HistoryEvent.OnInvoiceClick(invoice.id)) },
        )
        if (isExpanded) {
            HistoryInvoiceDetail(
                invoice = invoice,
                onDownloadPdf = { onEvent(HistoryEvent.OnDownloadPdf(invoice.id)) },
                onSend = { onEvent(HistoryEvent.OnSendInvoice(invoice.id)) },
                onCancel = { onEvent(HistoryEvent.OnCancelInvoice(invoice.id)) },
            )
        }
    }
}
