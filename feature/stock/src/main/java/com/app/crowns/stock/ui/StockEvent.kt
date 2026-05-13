package com.app.crowns.stock.ui

sealed class StockEvent {
    data object Refresh : StockEvent()
    data class OnFilterSelected(val filter: StockFilter) : StockEvent()
    data class OnSearchChanged(val query: String) : StockEvent()
    data class OnProductClick(val productId: String) : StockEvent()
    data object OnAddProduct : StockEvent()
}

sealed class StockNavigationEvent {
    data class NavigateToProductDetail(val productId: String) : StockNavigationEvent()
    data object NavigateToAddProduct : StockNavigationEvent()
}