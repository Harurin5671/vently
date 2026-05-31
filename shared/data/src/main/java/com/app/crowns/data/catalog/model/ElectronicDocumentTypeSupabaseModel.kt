package com.app.crowns.data.catalog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElectronicDocumentTypeSupabaseModel(
    @SerialName("code") val code: String,
    @SerialName("name") val name: String,
    @SerialName("short_name") val shortName: String,
    @SerialName("serie_prefix") val seriePrefix: String,
    @SerialName("is_active") val isActive: Boolean
)
