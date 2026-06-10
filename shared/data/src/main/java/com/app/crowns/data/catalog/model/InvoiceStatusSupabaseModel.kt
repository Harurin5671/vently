package com.app.crowns.data.catalog.model

import kotlinx.serialization.Serializable

@Serializable
data class InvoiceStatusSupabaseModel(
    val code: String,
    val name: String,
    val description: String,
)
