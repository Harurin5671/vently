package com.app.crowns.data.invoice.model

import kotlinx.serialization.Serializable

@Serializable
data class SendToSunatResponseModel(
    val code: String,
    val description: String
)
