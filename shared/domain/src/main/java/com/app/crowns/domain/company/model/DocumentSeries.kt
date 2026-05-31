package com.app.crowns.domain.company.model

data class DocumentSeries(
    val id: String? = null,
    val companyId: String,
    val documentType: String,
    val serie: String,
    val lastCorrelative: Int = 0,
    val isActive: Boolean = true
)
