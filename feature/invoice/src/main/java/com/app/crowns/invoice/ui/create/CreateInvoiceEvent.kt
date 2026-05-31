package com.app.crowns.invoice.ui.create

import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.invoice.domain.model.InvoiceItem
import com.app.crowns.invoice.domain.model.InvoiceType

sealed class CreateInvoiceEvent {
    data class OnInvoiceTypeChanged(val type: InvoiceType) : CreateInvoiceEvent()
    data class OnDocumentTypeChanged(val type: IdentityDocumentTypeEntity) : CreateInvoiceEvent()
    data class OnClientNameChanged(val value: String) : CreateInvoiceEvent()
    data class OnClientRucChanged(val value: String) : CreateInvoiceEvent()
    data object OnSearchRuc : CreateInvoiceEvent()
    data class OnAddItem(val item: InvoiceItem) : CreateInvoiceEvent()
    data class OnRemoveItem(val productId: String) : CreateInvoiceEvent()
    data class OnIncreaseQuantity(val productId: String) : CreateInvoiceEvent()
    data class OnDecreaseQuantity(val productId: String) : CreateInvoiceEvent()
    data object OnShowAddProductSheet : CreateInvoiceEvent()
    data object OnDismissAddProductSheet : CreateInvoiceEvent()
    data class OnProductSearchChanged(val query: String) : CreateInvoiceEvent()
    data class OnCategoryFilterChanged(val category: String?) : CreateInvoiceEvent()
    data object OnPreview : CreateInvoiceEvent()
    data object RefreshProducts : CreateInvoiceEvent()
}

sealed class CreateInvoiceNavigationEvent {
    data object NavigateToPreview : CreateInvoiceNavigationEvent()
    data object NavigateBack : CreateInvoiceNavigationEvent()
}