package com.app.crowns.data.invoice.mapper

import com.app.crowns.data.invoice.model.SignInvoiceSupabaseModel
import com.app.crowns.domain.invoice.entities.SignInvoiceResponseEntity

fun SignInvoiceSupabaseModel.toDomain(): SignInvoiceResponseEntity = SignInvoiceResponseEntity(
    success = success,
    invoiceId = invoiceId,
    signedXml = signedXml,
    status = status
)
