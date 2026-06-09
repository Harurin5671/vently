package com.app.crowns.domain.company.repository

import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.customer.model.PersonInfo
import com.app.crowns.network.NetworkResult

interface CompanyRepository {
    suspend fun getCompany(): Result<CompanyInfo?>
    suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo>
    suspend fun getPersonInfo(dni: String): NetworkResult<PersonInfo>
    suspend fun updateCertificate(base64: String): Result<Unit>
    suspend fun updateCertificatePassword(password: String): Result<Unit>
    suspend fun getDocumentSeries(companyId: String): Result<List<DocumentSeries>>
    suspend fun updateDocumentSeries(series: DocumentSeries): Result<Unit>
    suspend fun updateSolCredentials(username: String, password: String): Result<Unit>
    suspend fun updateCompany(company: CompanyInfo): Result<Unit>
    suspend fun saveCompany(companyInfo: CompanyInfo, logoUrl: String?): Result<Unit>
    suspend fun uploadLogo(imageBytes: ByteArray): Result<String>
}
