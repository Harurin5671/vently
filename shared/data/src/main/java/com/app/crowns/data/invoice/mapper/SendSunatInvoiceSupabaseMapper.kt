package com.app.crowns.data.invoice.mapper

import com.app.crowns.data.invoice.model.SendSunatInvoiceSupabaseModel
import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity
import com.app.crowns.domain.invoice.entities.SunatResponseDetailEntity

fun SendSunatInvoiceSupabaseModel.toDomain(): SendSunatResponseEntity = SendSunatResponseEntity(
    success = success,
    invoiceId = invoiceId,
    status = status,
    sunatCode = sunatCode,
    sunatDescription = sunatDescription,
    sunatResponse = sunatResponse.let { 
        SunatResponseDetailEntity(it.code, it.description)
    }
)
