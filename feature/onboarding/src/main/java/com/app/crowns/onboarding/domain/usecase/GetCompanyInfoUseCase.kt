package com.app.crowns.onboarding.domain.usecase

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.network.NetworkResult
import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class GetCompanyInfoUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(ruc: String): NetworkResult<CompanyInfo> =
        repository.getCompanyInfo(ruc)
}