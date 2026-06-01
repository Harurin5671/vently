package com.app.crowns.domain.invoice.entities

data class GenerateXmlRequestEntity(
    val companyId: String,
    val series: String,
    val issueDate: String,
    val paymentMethod: String,
    val customerDocType: String,
    val customerDocNumber: String,
    val customerLegalName: String,
    val items: List<GenerateXmlRequestItemEntity>
)

data class GenerateXmlRequestItemEntity(
    val productId: String,
    val quantity: Double
)
