package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.invoice.entities.SendSunatResponseEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import javax.inject.Inject

class SendToSunatUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: String): Result<SendSunatResponseEntity> {
        return repository.sendToSunat(invoiceId)
    }
}
