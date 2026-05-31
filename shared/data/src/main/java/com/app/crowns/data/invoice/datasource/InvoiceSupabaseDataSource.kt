package com.app.crowns.data.invoice.datasource

import com.app.crowns.data.invoice.model.InvoiceItemSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceSupabaseModel

interface InvoiceSupabaseDataSource {
    suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceSupabaseModel>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemSupabaseModel>>
}