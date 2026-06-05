package com.app.crowns.domain.invoice.entities

data class SendToSunatRequestEntity(
    val zipBase64: String,
    val fileName: String,
    val ruc: String,
    val user: String,
    val clave: String
)
