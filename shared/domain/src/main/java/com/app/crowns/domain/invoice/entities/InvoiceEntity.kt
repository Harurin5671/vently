package com.app.crowns.domain.invoice.entities

import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import kotlinx.serialization.Serializable
import kotlinx.serialization.Contextual

@Serializable
data class InvoiceEntity(
    val id: String,
    val companyId: String,
    val documentType: String,
    val serie: String,
    val correlative: Long,
    val total: String,
    val status: String,
    @Contextual val statusInfo: InvoiceStatusEntity? = null,
    val issueDate: String,
    val operationType: String,
    val currency: String,
    val paymentType: String,
    val totalDiscount: String,
    val totalExportacion: String,
    val totalExonerada: String,
    val totalInafecta: String,
    val totalGravada: String,
    val totalIgv: String,
    val xmlDocument: String,
    val items: List<InvoiceItemEntity>
)
