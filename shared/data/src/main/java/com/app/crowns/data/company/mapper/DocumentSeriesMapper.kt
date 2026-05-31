package com.app.crowns.data.company.mapper

import com.app.crowns.data.company.entity.DocumentSeriesEntity
import com.app.crowns.domain.company.model.DocumentSeries

fun DocumentSeriesEntity.toDomain(): DocumentSeries = DocumentSeries(
    id = id,
    companyId = companyId,
    documentType = documentType,
    serie = serie,
    lastCorrelative = lastCorrelative,
    isActive = isActive
)

fun DocumentSeries.toEntity(): DocumentSeriesEntity = DocumentSeriesEntity(
    id = id,
    companyId = companyId,
    documentType = documentType,
    serie = serie,
    lastCorrelative = lastCorrelative,
    isActive = isActive
)
