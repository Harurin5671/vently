package com.app.crowns.domain.invoice.repository

import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity

interface InvoiceRepository {
    suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceEntity>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemEntity>>
    suspend fun generateXml(request: GenerateXmlRequestEntity): Result<InvoiceXmlResponseEntity>
}
