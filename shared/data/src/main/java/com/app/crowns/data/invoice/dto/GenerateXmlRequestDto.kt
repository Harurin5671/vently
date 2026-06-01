package com.app.crowns.data.invoice.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GenerateXmlRequestDto(
    @SerialName("company_id") val companyId: String,
    val series: String,
    @SerialName("issue_date") val issueDate: String,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("customer_doc_type") val customerDocType: String,
    @SerialName("customer_doc_number") val customerDocNumber: String,
    @SerialName("customer_legal_name") val customerLegalName: String,
    val items: List<GenerateXmlRequestItemDto>
)

@Serializable
data class GenerateXmlRequestItemDto(
    @SerialName("product_id") val productId: String,
    val quantity: Double
)