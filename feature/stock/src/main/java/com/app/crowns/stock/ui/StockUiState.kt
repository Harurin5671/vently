package com.app.crowns.stock.ui

data class StockUiState(
    val isLoading: Boolean = false,
    val totalProducts: Int = 0,
    val lowStockCount: Int = 0,
    val noStockCount: Int = 0,
    val products: List<Product> = emptyList(),
    val filteredProducts: List<Product> = emptyList(),
    val selectedFilter: StockFilter = StockFilter.ALL,
    val searchQuery: String = "",
    val error: String? = null,
)

data class Product(
    val id: String,
    val name: String,
    val sku: String,
    val category: String,
    val quantity: Int,
    val price: Double,
    val colorHex: String = "#3B82F6",
)

enum class StockFilter(val label: String) {
    ALL("Todos"),
    LOW_STOCK("Stock bajo"),
    NO_STOCK("Sin stock"),
}

val Product.stockStatus: StockStatus
    get() = when {
        quantity == 0 -> StockStatus.NONE
        quantity <= 5 -> StockStatus.LOW
        else -> StockStatus.OK
    }

enum class StockStatus { OK, LOW, NONE }