package com.app.crowns.onboarding.data.datasource

import com.app.crowns.domain.company.model.CompanyInfo

interface OnboardingSupabaseDataSource {
    suspend fun saveCompany(companyInfo: CompanyInfo, logoUrl: String?): Result<Unit>
    suspend fun uploadLogo(imageBytes: ByteArray): Result<String>
    suspend fun getCompany(): Result<CompanyInfo?>
}