package com.app.crowns.invoice.ui.preview

import android.graphics.Bitmap
import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity

data class InvoicePreviewUiState(
    val isGeneratingPdf: Boolean = true,
    val pdfPages: List<Bitmap> = emptyList(),
    val showEmitDialog: Boolean = false,
    val isEmitting: Boolean = false,
    val showResultScreen: Boolean = false,
    val sunatResponse: SendSunatResponseEntity? = null,
    val error: String? = null,
    val documentId: String? = null,
    val series: String? = null,
    val correlative: String? = null,
    val invoiceId: String? = null,
    val isSigned: Boolean = false,
)
