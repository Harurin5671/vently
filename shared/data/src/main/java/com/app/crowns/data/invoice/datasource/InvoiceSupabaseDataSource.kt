package com.app.crowns.data.invoice.datasource

import com.app.crowns.data.invoice.dto.GenerateXmlRequestDto
import com.app.crowns.data.invoice.model.InvoiceItemSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceXmlResponseModel

interface InvoiceSupabaseDataSource {
    suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceSupabaseModel>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemSupabaseModel>>
    suspend fun generateXml(request: GenerateXmlRequestDto): Result<InvoiceXmlResponseModel>
}
