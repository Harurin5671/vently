package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class GetDocumentSeriesUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(companyId: String): Result<List<DocumentSeries>> {
        return repository.getDocumentSeries(companyId)
    }
}
