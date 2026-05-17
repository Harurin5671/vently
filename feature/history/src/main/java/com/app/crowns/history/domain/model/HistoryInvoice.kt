package com.app.crowns.history.domain.model

data class HistoryInvoice(
    val id: String,
    val number: String,
    val clientName: String,
    val clientRuc: String?,
    val amount: Double,
    val subtotal: Double,
    val igv: Double,
    val type: HistoryInvoiceType,
    val status: HistoryInvoiceStatus,
    val dateTime: String,
    val items: List<HistoryInvoiceItem>,
    val hash: String?,
)

data class HistoryInvoiceItem(
    val name: String,
    val quantity: Int,
    val unitPrice: Double,
) {
    val subtotal: Double get() = quantity * unitPrice
}