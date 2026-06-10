package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.repository.CompanyRepository
import com.app.crowns.network.NetworkResult
import javax.inject.Inject

class SearchCompanyUseCase @Inject constructor(
    private val repository: CompanyRepository,
) {
    suspend operator fun invoke(ruc: String): NetworkResult<CompanyInfo> =
        repository.getCompanyInfo(ruc)
}
