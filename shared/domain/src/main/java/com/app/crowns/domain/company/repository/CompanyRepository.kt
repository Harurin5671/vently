package com.app.crowns.domain.company.repository

import com.app.crowns.domain.company.model.CompanyInfo

interface CompanyRepository {
    suspend fun getCompany(): Result<CompanyInfo?>
}
