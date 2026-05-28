package com.app.crowns.data.company.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CompanyEntity(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("ruc") val ruc: String,
    @SerialName("business_name") val businessName: String,
    @SerialName("status") val status: String? = null,
    @SerialName("condition") val condition: String? = null,
    @SerialName("address") val address: String? = null,
    @SerialName("district") val district: String? = null,
    @SerialName("province") val province: String? = null,
    @SerialName("department") val department: String? = null,
    @SerialName("is_retention_agent") val isRetentionAgent: Boolean = false,
    @SerialName("is_good_taxpayer") val isGoodTaxpayer: Boolean = false,
    @SerialName("logo_url") val logoUrl: String? = null,
    @SerialName("certificate_base64") val certificateBase64: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
)