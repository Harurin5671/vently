package com.app.crowns.data.company.mapper

import com.app.crowns.data.company.entity.CompanyEntity
import com.app.crowns.domain.company.model.CompanyInfo

fun CompanyEntity.toDomain(): CompanyInfo = CompanyInfo(
    businessName = businessName,
    documentNumber = ruc,
    status = status.orEmpty(),
    condition = condition.orEmpty(),
    address = address.orEmpty(),
    district = district.orEmpty(),
    province = province.orEmpty(),
    department = department.orEmpty(),
    isRetentionAgent = isRetentionAgent,
    isGoodTaxpayer = isGoodTaxpayer,
    logoUrl = logoUrl,
)

fun CompanyInfo.toEntity(userId: String, logoUrl: String? = null): CompanyEntity = CompanyEntity(
    userId = userId,
    ruc = documentNumber,
    businessName = businessName,
    status = status,
    condition = condition,
    address = address,
    district = district,
    province = province,
    department = department,
    isRetentionAgent = isRetentionAgent,
    isGoodTaxpayer = isGoodTaxpayer,
    logoUrl = logoUrl ?: this.logoUrl,
)