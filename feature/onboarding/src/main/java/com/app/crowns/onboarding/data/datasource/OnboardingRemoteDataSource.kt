package com.app.crowns.onboarding.data.datasource

import com.app.crowns.data.company.dto.CompanyInfoDto
import com.app.crowns.network.NetworkResult

interface OnboardingRemoteDataSource {
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfoDto>
}