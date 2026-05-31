package com.app.crowns.data.catalog.repository

import com.app.crowns.data.catalog.datasource.CatalogDataSource
import com.app.crowns.data.catalog.mapper.toDomain
import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity
import com.app.crowns.domain.catalog.repository.CatalogRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatalogRepositoryImpl @Inject constructor(
    private val catalogDataSource: CatalogDataSource
): CatalogRepository {
    override suspend fun getIgvTypes(): Result<List<IgvTypeEntity>> = runCatching {
        catalogDataSource.getIgvTypes().getOrThrow().map { model ->
            model.toDomain()
        }
    }

    override suspend fun getUnitsOfMeasure(): Result<List<UnitOfMeasureEntity>> = runCatching {
        catalogDataSource.getUnitsOfMeasure().getOrThrow().map { model ->
            model.toDomain()
        }
    }

    override suspend fun getIdentityDocumentTypes(): Result<List<IdentityDocumentTypeEntity>> = runCatching {
        catalogDataSource.getIdentityDocumentTypes().getOrThrow().map { model ->
            model.toDomain()
        }
    }

    override suspend fun getInvoiceStatuses(): Result<List<InvoiceStatusEntity>> = runCatching {
        catalogDataSource.getInvoiceStatuses().getOrThrow().map { model ->
            model.toDomain()
        }
    }
}
