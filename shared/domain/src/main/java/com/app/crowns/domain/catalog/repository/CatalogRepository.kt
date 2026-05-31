package com.app.crowns.domain.catalog.repository

import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity

interface CatalogRepository {
    suspend fun getIgvTypes(): Result<List<IgvTypeEntity>>
    suspend fun getUnitsOfMeasure(): Result<List<UnitOfMeasureEntity>>
    suspend fun getIdentityDocumentTypes(): Result<List<IdentityDocumentTypeEntity>>
    suspend fun getElectronicDocumentTypes(): Result<List<ElectronicDocumentTypeEntity>>
    suspend fun getInvoiceStatuses(): Result<List<InvoiceStatusEntity>>
}