package com.app.crowns.onboarding.data.datasource

import com.app.crowns.network.NetworkResult
import com.app.crowns.onboarding.data.dto.CompanyInfoDto

interface OnboardingRemoteDataSource {
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfoDto>
}