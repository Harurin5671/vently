package com.app.crowns.invoice.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceItem(
    val productId: String,
    val name: String,
    val sku: String?,
    val unitPrice: Double,
    val quantity: Int,
) {
    val subtotal: Double get() = unitPrice * quantity
}