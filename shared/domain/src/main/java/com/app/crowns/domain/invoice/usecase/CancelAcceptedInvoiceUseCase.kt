package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.invoice.entities.CancelInvoiceResponseEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import javax.inject.Inject

class CancelAcceptedInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: String, reason: String): Result<CancelInvoiceResponseEntity> =
        repository.cancelAcceptedInvoice(invoiceId, reason)
}
