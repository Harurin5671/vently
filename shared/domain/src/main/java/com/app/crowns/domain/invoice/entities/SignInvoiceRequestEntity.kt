package com.app.crowns.domain.invoice.entities

import kotlinx.serialization.Serializable

@Serializable
data class SignInvoiceRequestEntity(
    val xmlDocument: String,
    val certificateBase64: String,
    val password: String,
    val ruc: String,
    val invoiceType: String,
    val serial: String,
    val correlative: String
)