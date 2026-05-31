package com.app.crowns.data.catalog.mapper

import com.app.crowns.data.catalog.model.ElectronicDocumentTypeSupabaseModel
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity

fun ElectronicDocumentTypeSupabaseModel.toDomain(): ElectronicDocumentTypeEntity = ElectronicDocumentTypeEntity(
    code = code,
    name = name,
    shortName = shortName,
    seriePrefix = seriePrefix,
    isActive = isActive
)
