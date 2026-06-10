package com.app.crowns.domain.invoice.entities

data class CancelInvoiceResponseEntity(
    val success: Boolean,
    val status: String,
    val voidDocumentId: String?,
    val sunatCode: String?,
    val sunatDescription: String?,
)
