package com.app.crowns.data.invoice.repository

import com.app.crowns.data.catalog.datasource.CatalogDataSource
import com.app.crowns.data.catalog.mapper.toDomain as toCatalogDomain
import com.app.crowns.data.invoice.datasource.InvoiceSupabaseDataSource
import com.app.crowns.data.invoice.mapper.toDomain
import com.app.crowns.data.invoice.mapper.toDto
import com.app.crowns.domain.invoice.entities.CancelInvoiceResponseEntity
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity
import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity
import com.app.crowns.domain.invoice.entities.SignInvoiceResponseEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceDataSource: InvoiceSupabaseDataSource,
    private val catalogDataSource: CatalogDataSource
) : InvoiceRepository {

    private val _invoiceChanges = MutableSharedFlow<Unit>(replay = 1)
    override val invoiceChanges = _invoiceChanges.asSharedFlow()
    override suspend fun getInvoicesHistory(
        companyId: String,
        startDate: String,
        endDate: String,
        offset: Int,
        limit: Int,
    ): Result<List<InvoiceEntity>> = runCatching {
        val models = invoiceDataSource.getInvoicesHistory(companyId, startDate, endDate, offset, limit).getOrThrow()
        val statuses = catalogDataSource.getInvoiceStatuses().getOrDefault(emptyList())
        models.map { model ->
            val statusInfo = statuses.find { it.code.equals(model.status, ignoreCase = true) }?.toCatalogDomain()
            model.toDomain().copy(statusInfo = statusInfo)
        }
    }

    override suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemEntity>> =
        runCatching {
            invoiceDataSource.getInvoiceItems(invoiceId).getOrThrow().map { model ->
                model.toDomain()
            }
        }

    override suspend fun generateXml(request: GenerateXmlRequestEntity): Result<InvoiceXmlResponseEntity> =
        runCatching {
            invoiceDataSource.generateXml(request.toDto()).getOrThrow().toDomain()
        }

    override suspend fun signInvoice(invoiceId: String): Result<SignInvoiceResponseEntity> = runCatching {
        invoiceDataSource.signInvoice(invoiceId).getOrThrow().toDomain()
    }

    override suspend fun sendToSunat(invoiceId: String): Result<SendSunatResponseEntity> {
        val result = runCatching {
            invoiceDataSource.sendToSunat(invoiceId).getOrThrow().toDomain()
        }
        // La factura ya existe y cambió de estado: avisar para refrescar dashboard/historial.
        if (result.isSuccess) _invoiceChanges.emit(Unit)
        return result
    }

    override suspend fun cancelInvoice(invoiceId: String): Result<Unit> =
        invoiceDataSource.cancelInvoice(invoiceId)

    override suspend fun cancelAcceptedInvoice(invoiceId: String, reason: String): Result<CancelInvoiceResponseEntity> =
        runCatching {
            val model = invoiceDataSource.cancelAcceptedInvoice(invoiceId, reason).getOrThrow()
            CancelInvoiceResponseEntity(
                success = model.success,
                status = model.status,
                voidDocumentId = model.voidDocumentId,
                sunatCode = model.sunatCode,
                sunatDescription = model.sunatDescription,
            )
        }
}
