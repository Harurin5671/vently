package com.app.crowns.domain.invoice.repository

import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity
import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity
import com.app.crowns.domain.invoice.entities.SignInvoiceResponseEntity

interface InvoiceRepository {
    suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceEntity>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemEntity>>
    suspend fun generateXml(request: GenerateXmlRequestEntity): Result<InvoiceXmlResponseEntity>
    suspend fun signInvoice(invoiceId: String): Result<SignInvoiceResponseEntity>
    suspend fun sendToSunat(invoiceId: String): Result<SendSunatResponseEntity>
}
