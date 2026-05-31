package com.app.crowns.data.company.entity

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DocumentSeriesEntity(
    @SerialName("id") val id: String? = null,
    @SerialName("company_id") val companyId: String,
    @SerialName("document_type") val documentType: String,
    @SerialName("serie") val serie: String,
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @EncodeDefault @SerialName("last_correlative") val lastCorrelative: Int = 0,
    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    @EncodeDefault @SerialName("is_active") val isActive: Boolean = true,
    @SerialName("created_at") val createdAt: String? = null
)
