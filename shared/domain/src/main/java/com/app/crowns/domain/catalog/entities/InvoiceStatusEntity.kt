package com.app.crowns.domain.catalog.entities

data class InvoiceStatusEntity(
    val code: String,
    val name: String,
    val description: String,
    val color: String
) {
    companion object {
        const val ACCEPTED = "ACEPTADA"
        const val PENDING = "PENDIENTE"
        const val CANCELLED = "ANULADA"
        const val DRAFT = "BORRADOR"
    }
}
