package com.app.crowns.domain.catalog.usecase

import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.domain.catalog.repository.CatalogRepository
import javax.inject.Inject

class GetIdentityDocumentTypesUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<IdentityDocumentTypeEntity>> = repository.getIdentityDocumentTypes()
}