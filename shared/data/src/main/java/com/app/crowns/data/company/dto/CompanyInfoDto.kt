package com.app.crowns.data.company.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyInfoDto(
    @SerialName("razon_social") val businessName: String,
    @SerialName("numero_documento") val documentNumber: String,
    @SerialName("estado") val status: String,
    @SerialName("condicion") val condition: String,
    @SerialName("direccion") val address: String,
    @SerialName("ubigeo") val ubigeo: String,
    @SerialName("via_tipo") val streetType: String,
    @SerialName("via_nombre") val streetName: String,
    @SerialName("zona_codigo") val zoneCode: String,
    @SerialName("zona_tipo") val zoneType: String,
    @SerialName("numero") val number: String,
    @SerialName("interior") val interior: String,
    @SerialName("lote") val lot: String,
    @SerialName("dpto") val apartment: String,
    @SerialName("manzana") val block: String,
    @SerialName("kilometro") val kilometer: String,
    @SerialName("distrito") val district: String,
    @SerialName("provincia") val province: String,
    @SerialName("departamento") val department: String,
    @SerialName("es_agente_retencion") val isRetentionAgent: Boolean,
    @SerialName("es_buen_contribuyente") val isGoodTaxpayer: Boolean,
    @SerialName("locales_anexos") val annexedLocations: String? = null,
)
