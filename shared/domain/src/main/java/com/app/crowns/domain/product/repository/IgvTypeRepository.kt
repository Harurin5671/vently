package com.app.crowns.domain.product.repository

import com.app.crowns.domain.product.entities.IgvTypeEntity

interface IgvTypeRepository {
    suspend fun getIgvType(): Result<List<IgvTypeEntity>>
}