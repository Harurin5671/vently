package com.app.crowns.data.catalog.datasource

import com.app.crowns.data.catalog.model.ElectronicDocumentTypeSupabaseModel
import com.app.crowns.data.catalog.model.IdentityDocumentTypeSupabaseModel
import com.app.crowns.data.catalog.model.IgvTypeSupabaseModel
import com.app.crowns.data.catalog.model.InvoiceStatusSupabaseModel
import com.app.crowns.data.catalog.model.UnitOfMeasureSupabaseModel

interface CatalogDataSource {
    suspend fun getIgvTypes(): Result<List<IgvTypeSupabaseModel>>
    suspend fun getUnitsOfMeasure(): Result<List<UnitOfMeasureSupabaseModel>>
    suspend fun getIdentityDocumentTypes(): Result<List<IdentityDocumentTypeSupabaseModel>>
    suspend fun getElectronicDocumentTypes(): Result<List<ElectronicDocumentTypeSupabaseModel>>
    suspend fun getInvoiceStatuses(): Result<List<InvoiceStatusSupabaseModel>>
}
