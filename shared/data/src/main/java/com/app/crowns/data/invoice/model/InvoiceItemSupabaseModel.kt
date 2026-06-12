package com.app.crowns.data.invoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InvoiceItemSupabaseModel(
    val id: String,
    @SerialName("invoice_id") val invoiceID: String,
    @SerialName("product_id") val productID: String? = null,
    val orden: Long = 0,
    val descripcion: String,
    val unidad: String,
    val cantidad: Double = 0.0,
    @SerialName("precio_unitario") val precioUnitario: Double = 0.0,
    @SerialName("discount_type") val discountType: JsonElement? = null,
    @SerialName("discount_amount") val discountAmount: Double = 0.0,
    @SerialName("valor_venta") val valorVenta: Double = 0.0,
    @SerialName("igv_amount") val igvAmount: Double = 0.0,
    val total: Double = 0.0,
    @SerialName("igv_type") val igvType: String
)