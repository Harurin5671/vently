package com.app.crowns.domain.invoice.entities

import kotlinx.serialization.Serializable

@Serializable
data class SendSunatResponseEntity(
    val success: Boolean,
    val invoiceId: String,
    val status: String,
    val sunatCode: String,
    val sunatDescription: String,
    val sunatResponse: SunatResponseDetailEntity? = null
) {
    val isSuccess: Boolean get() = success && sunatCode == "0"
}

@Serializable
data class SunatResponseDetailEntity(
    val code: String,
    val description: String
)
