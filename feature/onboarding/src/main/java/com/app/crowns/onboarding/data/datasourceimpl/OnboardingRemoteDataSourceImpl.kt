package com.app.crowns.onboarding.data.datasourceimpl

import com.app.crowns.network.NetworkResult
import com.app.crowns.network.client.HttpClient
import com.app.crowns.network.client.get
import com.app.crowns.network.di.ApiBaseUrl
import com.app.crowns.onboarding.data.datasource.OnboardingRemoteDataSource
import com.app.crowns.onboarding.data.dto.CompanyInfoDto
import javax.inject.Inject

class OnboardingRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
    @param:ApiBaseUrl private val baseUrl: String,
) : OnboardingRemoteDataSource {
    override suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfoDto> {
        return httpClient.get(
            url = "${baseUrl}get-ruc",
            queryParams = mapOf("ruc" to ruc),
        )
    }
}