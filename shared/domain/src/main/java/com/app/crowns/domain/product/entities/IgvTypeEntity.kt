package com.app.crowns.domain.product.entities

import kotlinx.serialization.Serializable

@Serializable
data class IgvTypeEntity(
    val code: String,
    val name: String,
    val taxCode: String,
    val percentage: Int,
    val factor: Double,
)