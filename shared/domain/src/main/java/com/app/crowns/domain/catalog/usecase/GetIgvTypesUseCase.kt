package com.app.crowns.domain.catalog.usecase

import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.repository.CatalogRepository
import javax.inject.Inject

class GetIgvTypesUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<IgvTypeEntity>> = repository.getIgvTypes()
}