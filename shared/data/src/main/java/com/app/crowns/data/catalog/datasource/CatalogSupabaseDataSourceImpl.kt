package com.app.crowns.data.catalog.datasource

import com.app.crowns.data.catalog.model.ElectronicDocumentTypeSupabaseModel
import com.app.crowns.data.catalog.model.IdentityDocumentTypeSupabaseModel
import com.app.crowns.data.catalog.model.IgvTypeSupabaseModel
import com.app.crowns.data.catalog.model.InvoiceStatusSupabaseModel
import com.app.crowns.data.catalog.model.UnitOfMeasureSupabaseModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class CatalogSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient
): CatalogDataSource {
    override suspend fun getIgvTypes(): Result<List<IgvTypeSupabaseModel>> = runCatching {
        supabase
            .from("cat_igv_types")
            .select()
            .decodeList<IgvTypeSupabaseModel>()
    }

    override suspend fun getUnitsOfMeasure(): Result<List<UnitOfMeasureSupabaseModel>> = runCatching {
        supabase
            .from("cat_unit_of_measure")
            .select()
            .decodeList<UnitOfMeasureSupabaseModel>()
    }

    override suspend fun getIdentityDocumentTypes(): Result<List<IdentityDocumentTypeSupabaseModel>> = runCatching {
        supabase
            .from("cat_identity_doc_types")
            .select()
            .decodeList<IdentityDocumentTypeSupabaseModel>()
    }

    override suspend fun getInvoiceStatuses(): Result<List<InvoiceStatusSupabaseModel>> = runCatching {
        supabase
            .from("cat_invoice_statuses")
            .select()
            .decodeList<InvoiceStatusSupabaseModel>()
    }

    override suspend fun getElectronicDocumentTypes(): Result<List<ElectronicDocumentTypeSupabaseModel>> = runCatching {
        supabase
            .from("cat_document_types")
            .select()
            .decodeList<ElectronicDocumentTypeSupabaseModel>()
    }
}