package com.app.crowns.onboarding.data.datasourceimpl

import android.util.Log
import com.app.crowns.data.company.dto.CompanyInfoDto
import com.app.crowns.network.NetworkResult
import com.app.crowns.network.client.HttpClient
import com.app.crowns.network.client.get
import com.app.crowns.network.di.ApiBaseUrl
import com.app.crowns.onboarding.data.datasource.OnboardingRemoteDataSource
import javax.inject.Inject

class OnboardingRemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
    @param:ApiBaseUrl private val baseUrl: String,
) : OnboardingRemoteDataSource {
    override suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfoDto> {
        Log.d("RUC_SEARCH", "Iniciando búsqueda de RUC: $ruc")
        Log.d("RUC_SEARCH", "URL: ${baseUrl}get-ruc")
        
        val result = httpClient.get<CompanyInfoDto>(
            url = "${baseUrl}get-ruc",
            queryParams = mapOf("ruc" to ruc),
        )
        
        Log.d("RUC_SEARCH", "Resultado de búsqueda para RUC $ruc: $result")
        return result
    }
}
