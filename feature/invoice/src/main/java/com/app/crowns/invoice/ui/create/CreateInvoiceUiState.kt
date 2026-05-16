package com.app.crowns.invoice.ui.create

import com.app.crowns.invoice.domain.model.InvoiceItem
import com.app.crowns.invoice.domain.model.InvoiceType

data class CreateInvoiceUiState(
    val invoiceType: InvoiceType = InvoiceType.BOLETA,
    val clientName: String = "",
    val clientRuc: String = "",
    val clientBusinessName: String = "",
    val isSearchingRuc: Boolean = false,
    val rucError: String? = null,
    val items: List<InvoiceItem> = emptyList(),
    val showAddProductSheet: Boolean = false,
    val productSearchQuery: String = "",
    val selectedCategoryFilter: String? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
)

val CreateInvoiceUiState.subtotal: Double
    get() = items.sumOf { it.subtotal } / 1.18
val CreateInvoiceUiState.igv: Double
    get() = items.sumOf { it.subtotal } - subtotal
val CreateInvoiceUiState.total: Double
    get() = items.sumOf { it.subtotal }
val CreateInvoiceUiState.isFormValid: Boolean
    get() = items.isNotEmpty() && when (invoiceType) {
        InvoiceType.BOLETA -> true
        InvoiceType.FACTURA -> clientRuc.length == 11 && clientBusinessName.isNotBlank()
    }