package com.app.crowns.domain.invoice.entities

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceXmlResponseEntity(
    val invoiceId: String,
    val documentId: String,
    val correlative: Int,
    val documentType: String,
    val status: String,
    val totals: InvoiceXmlTotalEntity,
    val xmlContent: String
)

@Serializable
data class InvoiceXmlTotalEntity(
    val subtotal: Double,
    val totalTaxed: Double,
    val totalExempt: Double,
    val totalUnaffected: Double,
    val taxAmount: Double,
    val total: Double,
)
