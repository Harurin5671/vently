package com.app.crowns.invoice.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class InvoiceType(val label: String, val prefix: String) {
    BOLETA("Boleta", "B"),
    FACTURA("Factura", "F"),
}