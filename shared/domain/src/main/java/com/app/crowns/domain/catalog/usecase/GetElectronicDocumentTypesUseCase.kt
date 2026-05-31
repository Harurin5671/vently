package com.app.crowns.domain.catalog.usecase

import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.catalog.repository.CatalogRepository
import javax.inject.Inject

class GetElectronicDocumentTypesUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<ElectronicDocumentTypeEntity>> {
        return repository.getElectronicDocumentTypes()
    }
}
