package com.app.crowns.data.company.mapper

import com.app.crowns.data.company.dto.CompanyInfoDto
import com.app.crowns.domain.company.model.CompanyInfo

fun CompanyInfoDto.toDomain(): CompanyInfo = CompanyInfo(
    businessName = businessName,
    documentNumber = documentNumber,
    status = status,
    condition = condition,
    address = address,
    district = district,
    province = province,
    department = department,
    isRetentionAgent = isRetentionAgent,
    isGoodTaxpayer = isGoodTaxpayer,
    logoUrl = null,
)