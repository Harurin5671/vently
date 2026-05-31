package com.app.crowns.domain.catalog.entities

data class ElectronicDocumentTypeEntity(
    val code: String,
    val name: String,
    val shortName: String,
    val seriePrefix: String,
    val isActive: Boolean
) {
    companion object {
        const val FACTURA = "01"
        const val BOLETA = "03"
    }
}
