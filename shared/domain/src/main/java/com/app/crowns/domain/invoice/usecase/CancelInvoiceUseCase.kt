package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.invoice.repository.InvoiceRepository
import javax.inject.Inject

class CancelInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: String): Result<Unit> =
        repository.cancelInvoice(invoiceId)
}
