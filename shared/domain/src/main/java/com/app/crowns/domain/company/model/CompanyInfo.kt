package com.app.crowns.domain.company.model

data class CompanyInfo(
    val id: String,
    val businessName: String,
    val documentNumber: String,
    val status: String,
    val condition: String,
    val address: String,
    val district: String,
    val province: String,
    val department: String,
    val isRetentionAgent: Boolean,
    val isGoodTaxpayer: Boolean,
    val logoUrl: String? = null,
    val certificateBase64: String? = null,
    val certificatePassword: String? = null,
)