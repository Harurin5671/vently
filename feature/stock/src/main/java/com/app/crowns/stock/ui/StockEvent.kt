package com.app.crowns.stock.ui

sealed class StockEvent {
    data object Refresh : StockEvent()
    data object SilentRefresh : StockEvent()
    data class OnFilterSelected(val filter: StockFilter) : StockEvent()
    data class OnSearchChanged(val query: String) : StockEvent()
    data class OnProductClick(val productId: String) : StockEvent()
    data class OnProductSelected(val product: com.app.crowns.domain.product.model.Product) : StockEvent()
    data object OnDismissProductDetail : StockEvent()
    data class OnEditProduct(val productId: String) : StockEvent()
    data object OnAddProduct : StockEvent()
}

sealed class StockNavigationEvent {
    data class NavigateToProductDetail(val productId: String) : StockNavigationEvent()
    data object NavigateToAddProduct : StockNavigationEvent()
    data class NavigateToEditProduct(val productId: String) : StockNavigationEvent()
}