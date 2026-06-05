package com.app.crowns.data.invoice.model

import kotlinx.serialization.Serializable

@Serializable
data class SignInvoiceRequestModel(
    val xmlDocument: String,
    val certificate: String,
    val password: String,
    val ruc: String,
    val invoiceType: String,
    val serial: String,
    val correlative: String
)