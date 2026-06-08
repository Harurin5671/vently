package com.app.crowns.stock.ui

import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity
import com.app.crowns.domain.product.model.Product

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
    val selectedProduct: Product? = null,
    val igvTypes: List<IgvTypeEntity> = emptyList(),
    val unitsOfMeasure: List<UnitOfMeasureEntity> = emptyList(),
    val isNavigatingToEdit: Boolean = false,
)

enum class StockFilter(val label: String) {
    ALL("Todos"),
    LOW_STOCK("Stock bajo"),
    NO_STOCK("Sin stock"),
}

val Product.stockStatus: StockStatus
    get() {
        if (!trackStock) return StockStatus.OK
        val threshold = minStockAlert ?: 5
        return when {
            currentStock == 0 -> StockStatus.NONE
            currentStock <= threshold -> StockStatus.LOW
            else -> StockStatus.OK
        }
    }

enum class StockStatus { OK, LOW, NONE }