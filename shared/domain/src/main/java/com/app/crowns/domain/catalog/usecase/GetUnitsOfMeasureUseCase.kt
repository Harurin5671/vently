package com.app.crowns.domain.catalog.usecase

import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity
import com.app.crowns.domain.catalog.repository.CatalogRepository
import javax.inject.Inject

class GetUnitsOfMeasureUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<UnitOfMeasureEntity>> = repository.getUnitsOfMeasure()
}