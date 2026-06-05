package com.app.crowns.data.invoice.mapper

import com.app.crowns.data.invoice.model.SignInvoiceRequestModel
import com.app.crowns.domain.invoice.entities.SignInvoiceRequestEntity

fun SignInvoiceRequestModel.toDomain(): SignInvoiceRequestEntity = SignInvoiceRequestEntity(
    xmlDocument = xmlDocument,
    certificateBase64 = certificate,
    password = password,
    ruc = ruc,
    invoiceType = invoiceType,
    serial = serial,
    correlative = correlative
)

fun SignInvoiceRequestEntity.toModel(): SignInvoiceRequestModel = SignInvoiceRequestModel(
    xmlDocument = xmlDocument,
    certificate = certificateBase64,
    password = password,
    ruc = ruc,
    invoiceType = invoiceType,
    serial = serial,
    correlative = correlative
)