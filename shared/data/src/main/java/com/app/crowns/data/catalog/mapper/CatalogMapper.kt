package com.app.crowns.data.catalog.mapper

import com.app.crowns.data.catalog.model.IdentityDocumentTypeSupabaseModel
import com.app.crowns.data.catalog.model.IgvTypeSupabaseModel
import com.app.crowns.data.catalog.model.InvoiceStatusSupabaseModel
import com.app.crowns.data.catalog.model.UnitOfMeasureSupabaseModel
import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity

fun IgvTypeSupabaseModel.toDomain(): IgvTypeEntity = IgvTypeEntity(
    code = code,
    name = name,
    taxCode = taxCode,
    percentage = percentage,
    factor = factor
)

fun UnitOfMeasureSupabaseModel.toDomain(): UnitOfMeasureEntity = UnitOfMeasureEntity(
    code = code,
    name = name,
    description = description
)

fun IdentityDocumentTypeSupabaseModel.toDomain(): IdentityDocumentTypeEntity = IdentityDocumentTypeEntity(
        code = code,
        name = name,
        maxLength = maxLength
    )

fun InvoiceStatusSupabaseModel.toDomain(): InvoiceStatusEntity = InvoiceStatusEntity(
    code = code,
    name = name,
    description = description,
    color = color
)
