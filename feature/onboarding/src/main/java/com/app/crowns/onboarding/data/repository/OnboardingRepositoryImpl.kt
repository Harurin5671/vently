package com.app.crowns.onboarding.data.repository

import com.app.crowns.network.NetworkResult
import com.app.crowns.network.map
import com.app.crowns.onboarding.data.datasource.OnboardingRemoteDataSource
import com.app.crowns.onboarding.data.dto.toDomain
import com.app.crowns.onboarding.domain.model.CompanyInfo
import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val remoteDataSource: OnboardingRemoteDataSource,
) : OnboardingRepository {
    override suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo> =
        remoteDataSource.getCompanyInfo(ruc).map { it.toDomain() }
}