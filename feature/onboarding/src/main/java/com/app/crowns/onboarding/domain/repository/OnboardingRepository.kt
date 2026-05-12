package com.app.crowns.onboarding.domain.repository

import com.app.crowns.network.NetworkResult
import com.app.crowns.onboarding.domain.model.CompanyInfo

interface OnboardingRepository {
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo>
}