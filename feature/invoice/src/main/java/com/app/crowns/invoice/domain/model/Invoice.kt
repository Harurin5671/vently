package com.app.crowns.invoice.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Invoice(
    val id: String? = null,
    val type: InvoiceType,
    val clientDocumentType: String? = null, // SUNAT code (1 for DNI, 6 for RUC, etc.)
    val clientName: String?,
    val clientRuc: String?,
    val clientBusinessName: String?,
    val items: List<InvoiceItem>,
    val createdAt: String? = null,
) {
    val subtotal: Double get() = items.sumOf { it.subtotal } / 1.18
    val igv: Double get() = items.sumOf { it.subtotal } - subtotal
    val total: Double get() = items.sumOf { it.subtotal }
}