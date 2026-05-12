package com.app.crowns.onboarding.data.datasourceimpl

import com.app.crowns.network.NetworkResult
import com.app.crowns.onboarding.data.datasource.OnboardingRemoteDataSource
import com.app.crowns.onboarding.data.dto.CompanyInfoDto
import kotlinx.coroutines.delay
import javax.inject.Inject

class OnboardingRemoteDataSourceImpl @Inject constructor() : OnboardingRemoteDataSource {
    override suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfoDto> {
        delay(2_000) // simulate network call
        return NetworkResult.Success(
            CompanyInfoDto(
                businessName = "TEXTIL CROW S.A.C.",
                documentType = "6",
                documentNumber = ruc,
                status = "ACTIVO",
                condition = "HABIDO",
                address = "AV. DANIEL ALCIDES CARRION MZA. D LOTE 11",
                ubigeo = "150135",
                streetType = "AV.",
                streetName = "DANIEL ALCIDES CARRION",
                zoneCode = "ASC.",
                zoneType = "SANTA ROSA DE PIEROLA",
                number = "-",
                interior = "-",
                lot = "11",
                apartment = "-",
                block = "D",
                kilometer = "-",
                district = "SAN MARTIN DE PORRES",
                province = "LIMA",
                department = "LIMA",
                isRetentionAgent = false,
                isGoodTaxpayer = false,
                annexedLocations = null,
            )
        )
    }
}