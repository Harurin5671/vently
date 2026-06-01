package com.app.crowns.data.invoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InvoiceXmlResponseModel(
    val success: Boolean,
    @SerialName("invoice_id") val invoiceId: String,
    @SerialName("document_id") val documentId: String,
    val correlative: Int,
    @SerialName("document_type") val documentType: String,
    val status: String,
    val totals: InvoiceXmlTotalResponseModel,
    val xml: String
)

@Serializable
data class InvoiceXmlTotalResponseModel(
    val subtotal: Double,
    @SerialName("total_taxed") val totalTaxed: Double,
    @SerialName("total_exempt") val totalExempt: Double,
    @SerialName("total_unaffected") val totalUnaffected: Double,
    @SerialName("total_igv") val totalIgv: Double,
    val total: Double,
)