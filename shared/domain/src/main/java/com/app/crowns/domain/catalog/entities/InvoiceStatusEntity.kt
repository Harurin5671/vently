package com.app.crowns.domain.catalog.entities

data class InvoiceStatusEntity(
    val code: String,
    val name: String,
    val description: String,
    val color: String = colorForCode(code),
) {
    companion object {
        const val ACCEPTED = "ACEPTADA"
        const val PENDING = "PENDIENTE"
        const val CANCELLED = "ANULADA"
        const val DRAFT = "BORRADOR"

        private val COLOR_MAP = mapOf(
            DRAFT     to "#9E9E9E",
            PENDING   to "#FF9800",
            ACCEPTED  to "#4CAF50",
            "OBSERVADA" to "#2196F3",
            "RECHAZADA" to "#F44336",
            CANCELLED to "#607D8B",
            "ERROR"   to "#E91E63",
        )

        fun colorForCode(code: String): String = COLOR_MAP[code] ?: "#9E9E9E"
    }
}
