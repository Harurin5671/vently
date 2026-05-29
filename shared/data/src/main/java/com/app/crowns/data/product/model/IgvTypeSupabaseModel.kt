package com.app.crowns.data.product.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IgvTypeSupabaseModel(
    val code: String,
    val name: String,
    @SerialName("tax_code") val taxCode: String,
    @SerialName("tax_type_code") val taxTypeCode: String,
    @SerialName("tax_type_name") val taxTypeName: String,
    val percentage: Int,
    val factor: Double,
    @SerialName("affects_igv") val affectsIgv: Boolean
)