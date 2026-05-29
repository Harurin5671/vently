package com.app.crowns.data.product.mapper

import com.app.crowns.data.product.model.IgvTypeSupabaseModel
import com.app.crowns.domain.product.entities.IgvTypeEntity

fun IgvTypeSupabaseModel.toDomain(): IgvTypeEntity = IgvTypeEntity(
    code = code,
    name = name,
    taxCode = taxCode,
    percentage = percentage,
    factor = factor
)