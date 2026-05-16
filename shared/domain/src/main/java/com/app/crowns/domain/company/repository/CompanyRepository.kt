package com.app.crowns.domain.company.repository

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.network.NetworkResult

interface CompanyRepository {
    suspend fun getCompany(): Result<CompanyInfo?>
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo>
}
