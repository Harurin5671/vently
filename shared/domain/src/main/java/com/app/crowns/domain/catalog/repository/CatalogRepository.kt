package com.app.crowns.domain.catalog.repository

import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity

interface CatalogRepository {
    suspend fun getIgvTypes(): Result<List<IgvTypeEntity>>
    suspend fun getUnitsOfMeasure(): Result<List<UnitOfMeasureEntity>>
    suspend fun getIdentityDocumentTypes(): Result<List<IdentityDocumentTypeEntity>>
    suspend fun getInvoiceStatuses(): Result<List<InvoiceStatusEntity>>
}
