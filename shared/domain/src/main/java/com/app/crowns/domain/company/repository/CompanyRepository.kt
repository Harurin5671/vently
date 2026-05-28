package com.app.crowns.domain.company.repository

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.network.NetworkResult

interface CompanyRepository {
    suspend fun getCompany(): Result<CompanyInfo?>
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo>
    suspend fun updateCertificate(base64: String): Result<Unit>
    suspend fun updateCertificatePassword(password: String): Result<Unit>
}
