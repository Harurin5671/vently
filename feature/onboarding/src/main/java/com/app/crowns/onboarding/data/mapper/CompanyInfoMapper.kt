package com.app.crowns.onboarding.data.mapper

import com.app.crowns.onboarding.data.dto.CompanyInfoDto
import com.app.crowns.onboarding.domain.model.CompanyInfo

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
)