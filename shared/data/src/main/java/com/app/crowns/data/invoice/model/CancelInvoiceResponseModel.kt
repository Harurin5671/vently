package com.app.crowns.data.invoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CancelInvoiceResponseModel(
    val success: Boolean,
    @SerialName("invoice_id") val invoiceId: String,
    val status: String,
    @SerialName("void_document_id") val voidDocumentId: String? = null,
    @SerialName("sunat_code") val sunatCode: String? = null,
    @SerialName("sunat_description") val sunatDescription: String? = null,
)
