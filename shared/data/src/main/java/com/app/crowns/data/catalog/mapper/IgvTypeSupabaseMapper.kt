package com.app.crowns.data.catalog.mapper

import com.app.crowns.data.catalog.model.IgvTypeSupabaseModel
import com.app.crowns.domain.catalog.entities.IgvTypeEntity

fun IgvTypeSupabaseModel.toDomains(): IgvTypeEntity = IgvTypeEntity(
    code = code,
    name = name,
    taxCode = taxCode,
    percentage = percentage,
    factor = factor
)