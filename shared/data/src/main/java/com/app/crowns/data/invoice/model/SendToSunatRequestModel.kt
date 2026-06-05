package com.app.crowns.data.invoice.model

import kotlinx.serialization.Serializable

@Serializable
data class SendToSunatRequestModel(
    val zipBase64: String,
    val fileName: String,
    val ruc: String,
    val user: String,
    val clave: String
)
