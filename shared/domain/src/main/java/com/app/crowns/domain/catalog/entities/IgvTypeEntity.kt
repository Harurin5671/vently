package com.app.crowns.domain.catalog.entities

import kotlinx.serialization.Serializable

@Serializable
data class IgvTypeEntity(
    val code: String,
    val name: String,
    val taxCode: String,
    val percentage: Int,
    val factor: Double,
) {
    /**
     * Indica si este tipo de afectación es válido para una venta (boleta/factura).
     * Solo los códigos de "Operación Onerosa" (10 Gravado, 20 Exonerado, 30 Inafecto)
     * representan una venta con precio. El resto del catálogo 07 son retiros,
     * donaciones, premios, bonificaciones y transferencias gratuitas, que SUNAT
     * rechaza en una línea con precio. Además son los únicos códigos que la Edge
     * Function generate-xml totaliza correctamente hoy.
     */
    val isSellableForSale: Boolean get() = code in SELLABLE_FOR_SALE_CODES

    companion object {
        val SELLABLE_FOR_SALE_CODES = setOf("10", "20", "30")
    }
}