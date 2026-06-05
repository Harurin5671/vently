package com.app.crowns.domain.invoice.entities

data class SunatResponseEntity(
    val code: String,
    val description: String
) {
    val isSuccess: Boolean get() = code == "0"
}
