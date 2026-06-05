package com.app.crowns.data.invoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SignInvoiceSupabaseModel(
    val success: Boolean,
    @SerialName("invoice_id") val invoiceId: String,
    @SerialName("signed_xml") val signedXml: String,
    val status: String,
)