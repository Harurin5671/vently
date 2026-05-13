package com.app.crowns.stock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(StockUiState())
    val state: StateFlow<StockUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<StockNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadStock()
    }

    fun onEvent(event: StockEvent) {
        when (event) {
            is StockEvent.Refresh -> loadStock()
            is StockEvent.OnFilterSelected -> applyFilter(event.filter)
            is StockEvent.OnSearchChanged -> applySearch(event.query)
            is StockEvent.OnProductClick -> navigateToDetail(event.productId)
            is StockEvent.OnAddProduct -> emitNav(StockNavigationEvent.NavigateToAddProduct)
        }
    }

    private fun loadStock() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // mock data — reemplazar con usecases reales
            val mockProducts = listOf(
                Product("1", "Agua Mineral 500ml", "BEB-0012", "Bebidas", 5, 2.50, "#3B82F6"),
                Product("2", "Polo Algodón M", "ROP-0941", "Ropa", 0, 45.00, "#10B981"),
                Product("3", "Gaseosa Inka 1.5L", "BEB-0412", "Bebidas", 24, 7.50, "#3B82F6"),
                Product("4", "Galletas Soda Pack", "SNA-0052", "Snacks", 48, 1.20, "#F59E0B"),
                Product("5", "Jean Slim Fit 32", "ROP-0231", "Ropa", 12, 89.90, "#10B981"),
            )
            _state.update {
                it.copy(
                    isLoading = false,
                    products = mockProducts,
                    filteredProducts = mockProducts,
                    totalProducts = mockProducts.size,
                    lowStockCount = mockProducts.count { p -> p.stockStatus == StockStatus.LOW },
                    noStockCount = mockProducts.count { p -> p.stockStatus == StockStatus.NONE },
                )
            }
        }
    }

    private fun applyFilter(filter: StockFilter) {
        _state.update { state ->
            val filtered = when (filter) {
                StockFilter.ALL -> state.products
                StockFilter.LOW_STOCK -> state.products.filter { it.stockStatus == StockStatus.LOW }
                StockFilter.NO_STOCK -> state.products.filter { it.stockStatus == StockStatus.NONE }
            }
            state.copy(selectedFilter = filter, filteredProducts = filtered)
        }
    }

    private fun applySearch(query: String) {
        _state.update { state ->
            val filtered = if (query.isBlank()) state.products
            else state.products.filter {
                it.name.contains(query, ignoreCase = true) ||
                        it.sku.contains(query, ignoreCase = true)
            }
            state.copy(searchQuery = query, filteredProducts = filtered)
        }
    }

    private fun navigateToDetail(productId: String) {
        emitNav(StockNavigationEvent.NavigateToProductDetail(productId))
    }

    private fun emitNav(event: StockNavigationEvent) {
        viewModelScope.launch { _navigationEvent.send(event) }
    }
}