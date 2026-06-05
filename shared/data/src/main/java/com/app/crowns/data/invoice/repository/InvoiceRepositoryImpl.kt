package com.app.crowns.data.invoice.repository

import android.util.Log
import com.app.crowns.data.catalog.datasource.CatalogDataSource
import com.app.crowns.data.catalog.mapper.toDomain as toCatalogDomain
import com.app.crowns.data.invoice.datasource.InvoiceSupabaseDataSource
import com.app.crowns.data.invoice.mapper.toDomain
import com.app.crowns.data.invoice.mapper.toDto
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity
import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity
import com.app.crowns.domain.invoice.entities.SignInvoiceResponseEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InvoiceRepositoryImpl @Inject constructor(
    private val invoiceDataSource: InvoiceSupabaseDataSource,
    private val catalogDataSource: CatalogDataSource
) : InvoiceRepository {
    override suspend fun getInvoicesHistory(companyId: String): Result<List<InvoiceEntity>> =
        runCatching {
            Log.d("INVOICE_REPO", "Solicitando historial al DataSource para: $companyId")
            val models = invoiceDataSource.getInvoicesHistory(companyId).getOrThrow()
            
            // Traer catálogos de estados para enriquecer la entidad
            val statuses = catalogDataSource.getInvoiceStatuses().getOrDefault(emptyList())

            Log.d("INVOICE_REPO", "Modelos recibidos: ${models.size}. Mapeando a dominio...")
            val domain = models.map { model ->
                val statusInfo = statuses.find { it.code.equals(model.status, ignoreCase = true) }?.toCatalogDomain()
                model.toDomain().copy(statusInfo = statusInfo)
            }
            Log.d("INVOICE_REPO", "Mapeo completado. Total: ${domain.size}")
            domain
        }.onFailure {
            Log.e("INVOICE_REPO", "Error en InvoiceRepositoryImpl", it)
        }

    override suspend fun getInvoiceItems(invoiceId: String): Result<List<InvoiceItemEntity>> =
        runCatching {
            invoiceDataSource.getInvoiceItems(invoiceId).getOrThrow().map { model ->
                model.toDomain()
            }
        }

    override suspend fun generateXml(request: GenerateXmlRequestEntity): Result<InvoiceXmlResponseEntity> =
        runCatching {
            Log.d("Prueba_Generate_Xml", "REPO: Iniciando proceso de generación")
            Log.d("Prueba_Generate_Xml", "REPO: Mapeando Entity a DTO")
            val dto = request.toDto()
            
            Log.d("Prueba_Generate_Xml", "REPO: Llamando al DataSource")
            val responseModel = invoiceDataSource.generateXml(dto).getOrThrow()
            
            Log.d("Prueba_Generate_Xml", "REPO: Mapeando respuesta a Entity de Dominio")
            val entity = responseModel.toDomain()
            Log.d("Prueba_Generate_Xml", "REPO: Proceso completado exitosamente")
            entity
        }.onFailure {
            Log.e("Prueba_Generate_Xml", "REPO: Error en el flujo del repositorio", it)
        }

    override suspend fun signInvoice(invoiceId: String): Result<SignInvoiceResponseEntity> = runCatching {
        Log.d("Prueba_Sign_Invoice", "REPO: Iniciando firma para invoiceId: $invoiceId")
        invoiceDataSource.signInvoice(invoiceId).getOrThrow().toDomain()
    }.onFailure {
        Log.e("Prueba_Sign_Invoice", "REPO: Error en firma", it)
    }

    override suspend fun sendToSunat(invoiceId: String): Result<SendSunatResponseEntity> = runCatching {
        Log.d("Prueba_Send_Sunat", "REPO: Enviando a SUNAT para invoiceId: $invoiceId")
        invoiceDataSource.sendToSunat(invoiceId).getOrThrow().toDomain()
    }.onFailure {
        Log.e("Prueba_Send_Sunat", "REPO: Error en envío a SUNAT", it)
    }
}
