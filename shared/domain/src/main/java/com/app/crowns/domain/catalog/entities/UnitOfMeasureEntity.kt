package com.app.crowns.domain.catalog.entities

import kotlinx.serialization.Serializable

@Serializable
data class UnitOfMeasureEntity(
    val code: String,
    val name: String,
    val description: String
)