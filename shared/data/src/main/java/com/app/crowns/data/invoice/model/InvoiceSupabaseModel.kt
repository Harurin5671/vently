package com.app.crowns.data.invoice.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class InvoiceSupabaseModel(
    val id: String,
    @SerialName("company_id") val companyId: String,
    @SerialName("customer_id") val customerId: JsonElement? = null,
    @SerialName("document_type") val documentType: String,
    val serie: String,
    val correlative: Long = 0,
    @SerialName("issue_date") val issueDate: String,
    @SerialName("due_date") val dueDate: JsonElement? = null,
    @SerialName("operation_type") val operationType: String,
    val currency: String,
    @SerialName("payment_type") val paymentType: String,
    @SerialName("subtotal") val subTotal: Double = 0.0,
    @SerialName("total_discount") val totalDiscount: Double = 0.0,
    @SerialName("total_exportacion") val totalExportacion: Double = 0.0,
    @SerialName("total_exonerada") val totalExonerada: Double = 0.0,
    @SerialName("total_inafecta") val totalInafecta: Double = 0.0,
    @SerialName("total_gravada") val totalGravada: Double = 0.0,
    @SerialName("total_igv") val totalIgv: Double = 0.0,
    val total: Double = 0.0,
    @SerialName("xml_document") val xmlDocument: String? = null,
    @SerialName("xml_signed_base64") val xmlSignedBase64: JsonElement? = null,
    val status: String,
    @SerialName("sunat_response") val sunatResponse: JsonElement? = null,
    @SerialName("sunat_code") val sunatCode: JsonElement? = null,
    @SerialName("sunat_description") val sunatDescription: JsonElement? = null,
    val hash: JsonElement? = null,
    val notes: JsonElement? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("updated_at") val updatedAt: String,
    @SerialName("invoice_items") val items: List<InvoiceItemSupabaseModel> = emptyList(),
    @SerialName("customer_doc_type") val customerDocType: String? = null,
    @SerialName("customer_doc_number") val customerDocNumber: String? = null,
    @SerialName("customer_legal_name") val customerLegalName: String? = null,
    @SerialName("customer_address") val customerAddress: String? = null,
    @SerialName("customer_email") val customerEmail: String? = null,
)
