package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.invoice.entities.SignInvoiceResponseEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import javax.inject.Inject

class SignInvoiceUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(invoiceId: String): Result<SignInvoiceResponseEntity> {
        return repository.signInvoice(invoiceId)
    }
}
