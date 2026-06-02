package com.app.crowns.data.invoice.mapper

import com.app.crowns.data.invoice.dto.GenerateXmlRequestDto
import com.app.crowns.data.invoice.dto.GenerateXmlRequestItemDto
import com.app.crowns.data.invoice.model.InvoiceXmlResponseModel
import com.app.crowns.data.invoice.model.InvoiceXmlTotalResponseModel
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.GenerateXmlRequestItemEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlTotalEntity

fun GenerateXmlRequestEntity.toDto(): GenerateXmlRequestDto = GenerateXmlRequestDto(
    companyId = companyId,
    series = series,
    issueDate = issueDate,
    currency = currency,
    paymentMethod = paymentMethod,
    customerDocType = customerDocType,
    customerDocNumber = customerDocNumber,
    customerLegalName = customerLegalName,
    items = items.map { it.toDto() }
)

fun GenerateXmlRequestItemEntity.toDto(): GenerateXmlRequestItemDto = GenerateXmlRequestItemDto(
    productId = productId,
    quantity = quantity
)

fun InvoiceXmlResponseModel.toDomain(): InvoiceXmlResponseEntity = InvoiceXmlResponseEntity(
    invoiceId = invoiceId,
    documentId = documentId,
    correlative = correlative,
    documentType = documentType,
    status = status,
    totals = totals.toDomain(),
    xmlContent = xml
)

fun InvoiceXmlTotalResponseModel.toDomain(): InvoiceXmlTotalEntity = InvoiceXmlTotalEntity(
    subtotal = subtotal,
    totalTaxed = totalTaxed,
    totalExempt = totalExempt,
    totalUnaffected = totalUnaffected,
    taxAmount = totalIgv,
    total = total
)
