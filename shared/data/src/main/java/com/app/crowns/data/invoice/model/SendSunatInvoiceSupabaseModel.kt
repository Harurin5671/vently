package com.app.crowns.data.invoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SendSunatInvoiceSupabaseModel(
    val success: Boolean,
    @SerialName("invoice_id") val invoiceId: String,
    val status: String,
    @SerialName("sunat_code") val sunatCode: String,
    @SerialName("sunat_description") val sunatDescription: String,
    @SerialName("sunat_response") val sunatResponse: SendSunatResponseSupabaseModel
)

@Serializable
data class SendSunatResponseSupabaseModel(
    val code: String,
    val description: String
)