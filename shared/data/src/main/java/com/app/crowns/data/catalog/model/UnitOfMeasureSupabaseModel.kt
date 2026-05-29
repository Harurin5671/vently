package com.app.crowns.data.catalog.model

import kotlinx.serialization.Serializable

@Serializable
data class UnitOfMeasureSupabaseModel(
    val code: String,
    val name: String,
    val description: String
)