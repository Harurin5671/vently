package com.app.crowns.data.company.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyInfoDto(
    @SerialName("razonSocial") val businessName: String,
    @SerialName("tipoDocumento") val documentType: String,
    @SerialName("numeroDocumento") val documentNumber: String,
    @SerialName("estado") val status: String,
    @SerialName("condicion") val condition: String,
    @SerialName("direccion") val address: String,
    @SerialName("ubigeo") val ubigeo: String,
    @SerialName("viaTipo") val streetType: String,
    @SerialName("viaNombre") val streetName: String,
    @SerialName("zonaCodigo") val zoneCode: String,
    @SerialName("zonaTipo") val zoneType: String,
    @SerialName("numero") val number: String,
    @SerialName("interior") val interior: String,
    @SerialName("lote") val lot: String,
    @SerialName("dpto") val apartment: String,
    @SerialName("manzana") val block: String,
    @SerialName("kilometro") val kilometer: String,
    @SerialName("distrito") val district: String,
    @SerialName("provincia") val province: String,
    @SerialName("departamento") val department: String,
    @SerialName("EsAgenteRetencion") val isRetentionAgent: Boolean,
    @SerialName("EsBuenContribuyente") val isGoodTaxpayer: Boolean,
    @SerialName("localesAnexos") val annexedLocations: String? = null,
)