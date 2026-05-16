package com.app.crowns.invoice.domain.model

data class Invoice(
    val id: String? = null,
    val type: InvoiceType,
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