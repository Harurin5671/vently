package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class UpdateDocumentSeriesUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(series: DocumentSeries): Result<Unit> {
        return repository.updateDocumentSeries(series)
    }
}
