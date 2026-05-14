package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class GetCompanyUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(): Result<CompanyInfo?> {
        return repository.getCompany()
    }
}
