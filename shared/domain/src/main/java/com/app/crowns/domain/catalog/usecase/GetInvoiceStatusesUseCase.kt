package com.app.crowns.domain.catalog.usecase

import com.app.crowns.domain.catalog.entities.InvoiceStatusEntity
import com.app.crowns.domain.catalog.repository.CatalogRepository
import javax.inject.Inject

class GetInvoiceStatusesUseCase @Inject constructor(
    private val repository: CatalogRepository
) {
    suspend operator fun invoke(): Result<List<InvoiceStatusEntity>> = repository.getInvoiceStatuses()
}
