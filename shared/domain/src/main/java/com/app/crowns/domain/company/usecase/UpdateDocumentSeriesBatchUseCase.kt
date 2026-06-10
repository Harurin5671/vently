package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class UpdateDocumentSeriesBatchUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(series: List<DocumentSeries>): Result<Unit> =
        repository.updateDocumentSeriesBatch(series)
}
