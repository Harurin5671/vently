package com.app.crowns.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun InvoiceActionButtons(
    onDownloadPdf: () -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        VentlyOutlineButton(
            label = "Descargar PDF",
            onClick = onDownloadPdf,
            modifier = Modifier.weight(1f),
        )
        VentlyOutlineButton(
            label = "Enviar",
            onClick = onSend,
            modifier = Modifier.weight(1f),
        )
    }
}
