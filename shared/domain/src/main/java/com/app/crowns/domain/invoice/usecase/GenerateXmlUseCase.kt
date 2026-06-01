package com.app.crowns.domain.invoice.usecase

import com.app.crowns.domain.invoice.entities.GenerateXmlRequestEntity
import com.app.crowns.domain.invoice.entities.InvoiceXmlResponseEntity
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import javax.inject.Inject

class GenerateXmlUseCase @Inject constructor(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(request: GenerateXmlRequestEntity): Result<InvoiceXmlResponseEntity> {
        return repository.generateXml(request)
    }
}
