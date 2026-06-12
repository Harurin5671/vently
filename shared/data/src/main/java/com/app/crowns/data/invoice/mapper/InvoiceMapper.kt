package com.app.crowns.data.invoice.mapper

import com.app.crowns.data.invoice.model.InvoiceItemSupabaseModel
import com.app.crowns.data.invoice.model.InvoiceSupabaseModel
import com.app.crowns.domain.invoice.entities.InvoiceEntity
import com.app.crowns.domain.invoice.entities.InvoiceItemEntity

fun InvoiceItemSupabaseModel.toDomain(): InvoiceItemEntity = InvoiceItemEntity(
    id = id,
    invoiceId = invoiceID,
    productId = productID ?: "",
    order = orden,
    description = descripcion,
    unit = unidad,
    quantity = cantidad.toString(),
    unitPrice = precioUnitario.toString(),
    discountType = discountType,
    discountAmount = discountAmount.toString(),
    salePrice = valorVenta.toString(),
    igvAmount = igvAmount.toString(),
    total = total.toString(),
    igvType = igvType
)

fun InvoiceSupabaseModel.toDomain(): InvoiceEntity = InvoiceEntity(
    id = id,
    companyId = companyId,
    documentType = documentType,
    serie = serie,
    correlative = correlative,
    total = total.toString(),
    status = status,
    issueDate = issueDate,
    operationType = operationType,
    currency = currency,
    paymentType = paymentType,
    totalDiscount = totalDiscount.toString(),
    totalExportacion = totalExportacion.toString(),
    totalExonerada = totalExonerada.toString(),
    totalInafecta = totalInafecta.toString(),
    totalGravada = totalGravada.toString(),
    totalIgv = totalIgv.toString(),
    xmlDocument = xmlDocument ?: "",
    items = items.map { it.toDomain() },
    customerDocNumber = customerDocNumber ?: "",
    customerLegalName = customerLegalName ?: "",
    customerAddress = customerAddress,
    customerEmail = customerEmail
)
