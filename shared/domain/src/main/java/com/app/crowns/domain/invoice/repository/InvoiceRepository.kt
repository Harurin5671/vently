package com.app.crowns.domain.invoice.repository

import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity

interface InvoiceRepository {
    suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceEntity>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemEntity>>
}