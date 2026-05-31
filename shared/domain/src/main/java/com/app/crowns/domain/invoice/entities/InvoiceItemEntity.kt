package com.app.crowns.domain.invoice.entities

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InvoiceItemEntity(
    val id: String,
    val invoiceId: String,
    val productId: String,
    val order: Long,
    val description: String,
    val unit: String,
    val quantity: String,
    val unitPrice: String,
    val discountType: JsonElement? = null,
    val discountAmount: String,
    val salePrice: String,
    val igvAmount: String,
    val total: String,
    val igvType: String
)