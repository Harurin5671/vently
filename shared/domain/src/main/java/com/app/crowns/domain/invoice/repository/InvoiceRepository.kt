package com.app.crowns.domain.invoice.repository

import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity
import com.app.crowns.domain.invoice.entities.CancelInvoiceResponseEntity
import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity
import com.app.crowns.domain.invoice.entities.SignInvoiceResponseEntity
import kotlinx.coroutines.flow.SharedFlow

interface InvoiceRepository {
    /** Emite cuando una factura/boleta se crea o cambia de estado (p.ej. tras emitir),
     *  para que las pantallas que muestran facturas (dashboard, historial) se recarguen. */
    val invoiceChanges: SharedFlow<Unit>

    suspend fun getInvoicesHistory(
        companyId: String,
        startDate: String,
        endDate: String,
        offset: Int,
        limit: Int,
    ): Result<List<InvoiceEntity>>
    suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemEntity>>
    suspend fun generateXml(request: GenerateXmlRequestEntity): Result<InvoiceXmlResponseEntity>
    suspend fun signInvoice(invoiceId: String): Result<SignInvoiceResponseEntity>
    suspend fun sendToSunat(invoiceId: String): Result<SendSunatResponseEntity>
    suspend fun cancelInvoice(invoiceId: String): Result<Unit>
    suspend fun cancelAcceptedInvoice(invoiceId: String, reason: String): Result<CancelInvoiceResponseEntity>
}
