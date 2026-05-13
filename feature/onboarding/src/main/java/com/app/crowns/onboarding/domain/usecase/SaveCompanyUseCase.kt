package com.app.crowns.onboarding.domain.usecase

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class SaveCompanyUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(
        companyInfo: CompanyInfo,
        logoUrl: String? = null,
    ): Result<Unit> = repository.saveCompany(companyInfo, logoUrl)
}