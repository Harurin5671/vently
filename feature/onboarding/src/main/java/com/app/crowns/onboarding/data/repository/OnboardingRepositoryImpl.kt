package com.app.crowns.onboarding.data.repository

import com.app.crowns.data.company.mapper.toDomain
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.network.NetworkResult
import com.app.crowns.network.map
import com.app.crowns.onboarding.data.datasource.OnboardingRemoteDataSource
import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val remoteDataSource: OnboardingRemoteDataSource,
    private val companyRepository: com.app.crowns.domain.company.repository.CompanyRepository,
) : OnboardingRepository {
    override suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo> =
        remoteDataSource.getCompanyInfo(ruc).map { it.toDomain() }

    override suspend fun saveCompany(
        companyInfo: CompanyInfo,
        logoUrl: String?
    ): Result<Unit> = companyRepository.saveCompany(companyInfo, logoUrl)

    override suspend fun uploadLogo(
        imageBytes: ByteArray
    ): Result<String> = companyRepository.uploadLogo(imageBytes)

    override suspend fun getCompany(): Result<CompanyInfo?> = companyRepository.getCompany()
}