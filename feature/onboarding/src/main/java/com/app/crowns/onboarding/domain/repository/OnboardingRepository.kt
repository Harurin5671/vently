package com.app.crowns.onboarding.domain.repository

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.network.NetworkResult

interface OnboardingRepository {
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo>
    suspend fun saveCompany(companyInfo: CompanyInfo, logoUrl: String?): Result<Unit>
    suspend fun uploadLogo(imageBytes: ByteArray): Result<String>
    suspend fun getCompany(): Result<CompanyInfo?>
}