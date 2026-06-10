package com.app.crowns.data.invoice.datasource

import com.app.crowns.data.invoice.dto.GenerateXmlRequestDto
import com.app.crowns.data.invoice.model.InvoiceItemSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceXmlResponseModel
import com.app.crowns.data.invoice.model.SendSunatInvoiceSupabaseModel
import com.app.crowns.data.invoice.model.SendToSunatRequestModel
import com.app.crowns.data.invoice.model.SendToSunatResponseModel
import com.app.crowns.data.invoice.model.CancelInvoiceResponseModel
import com.app.crowns.data.invoice.model.SignInvoiceRequestModel
import com.app.crowns.data.invoice.model.SignInvoiceSupabaseModel

interface InvoiceSupabaseDataSource {
    suspend fun getInvoicesHistory(
        companyId: String,
        startDate: String,
        endDate: String,
        offset: Int,
        limit: Int,
    ): Result<List<InvoiceSupabaseModel>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemSupabaseModel>>
    suspend fun generateXml(request: GenerateXmlRequestDto): Result<InvoiceXmlResponseModel>
    suspend fun signInvoice(invoiceId: String): Result<SignInvoiceSupabaseModel>
    suspend fun sendToSunat(invoiceId: String): Result<SendSunatInvoiceSupabaseModel>
    suspend fun cancelInvoice(invoiceId: String): Result<Unit>
    suspend fun cancelAcceptedInvoice(invoiceId: String, reason: String): Result<CancelInvoiceResponseModel>
}
