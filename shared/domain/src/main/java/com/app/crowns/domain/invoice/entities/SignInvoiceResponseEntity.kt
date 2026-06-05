package com.app.crowns.domain.invoice.entities

import kotlinx.serialization.Serializable

@Serializable
data class SignInvoiceResponseEntity(
    val success: Boolean,
    val invoiceId: String,
    val signedXml: String,
    val status: String,
)
