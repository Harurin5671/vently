package com.app.crowns.onboarding.domain.usecase

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetCompanyUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(): Result<CompanyInfo?> =
        repository.getCompany()
}