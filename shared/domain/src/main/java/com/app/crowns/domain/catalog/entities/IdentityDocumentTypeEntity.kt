package com.app.crowns.domain.catalog.entities

import android.health.connect.datatypes.units.Length
import kotlinx.serialization.Serializable

@Serializable
data class IdentityDocumentTypeEntity(
    val code: String,
    val name: String,
    val maxLength: Long? = null,
)