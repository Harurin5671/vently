package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class HasCompanyUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(): Boolean {
        return repository.getCompany()
            .getOrNull() != null
    }
}
