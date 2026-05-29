package com.app.crowns.data.catalog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IdentityDocumentTypeSupabaseModel(
    val code: String,
    val name: String,
    @SerialName("max_length") val maxLength: Long? = null,
    @SerialName("is_for_person") val isForPerson: Boolean,
    @SerialName("is_for_company") val isForCompany: Boolean
)