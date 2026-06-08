package com.app.crowns.invoice.ui.create

import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.invoice.domain.model.InvoiceItem
import com.app.crowns.invoice.domain.model.InvoiceType

data class CreateInvoiceUiState(
    val invoiceType: InvoiceType = InvoiceType.BOLETA,
    val selectedDocumentType: IdentityDocumentTypeEntity? = null,
    val documentTypes: List<IdentityDocumentTypeEntity> = emptyList(),
    val clientName: String = "",
    val clientRuc: String = "",
    val clientBusinessName: String = "",
    val isSearchingRuc: Boolean = false,
    val rucError: String? = null,
    val items: List<InvoiceItem> = emptyList(),
    val availableProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val showAddProductSheet: Boolean = false,
    val productSearchQuery: String = "",
    val selectedCategoryFilter: String? = null,
    val isLoadingProducts: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)

val CreateInvoiceUiState.subtotal: Double
    get() = items.sumOf { it.subtotal } / 1.18
val CreateInvoiceUiState.igv: Double
    get() = items.sumOf { it.subtotal } - subtotal
val CreateInvoiceUiState.total: Double
    get() = items.sumOf { it.subtotal }

val CreateInvoiceUiState.showBoletaLimitWarning: Boolean
    get() = invoiceType == InvoiceType.BOLETA && selectedDocumentType?.isNone == true && total >= 700.0

val CreateInvoiceUiState.isFormValid: Boolean
    get() = items.isNotEmpty() && when (invoiceType) {
        InvoiceType.BOLETA -> {
            val isTotalHigh = total >= 700.0
            val doc = selectedDocumentType
            doc != null && when {
                doc.isNone -> !isTotalHigh
                else -> doc.validate(clientRuc, clientName, clientBusinessName) && rucError == null
            }
        }
        InvoiceType.FACTURA -> {
            val doc = selectedDocumentType
            doc != null && doc.isRuc &&
                    clientRuc.length == 11 &&
                    (clientRuc.startsWith("10") || clientRuc.startsWith("20")) &&
                    clientBusinessName.isNotBlank() &&
                    rucError == null
        }
    }
