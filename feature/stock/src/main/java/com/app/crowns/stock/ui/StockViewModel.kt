package com.app.crowns.stock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.product.usecase.GetProductsUseCase
import com.app.crowns.domain.product.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StockViewModel @Inject constructor(
    private val getProductsUseCase: GetProductsUseCase,
    private val repository: ProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StockUiState())
    val state: StateFlow<StockUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<StockNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadStock()
        
        repository.productChanges
            .onEach { 
                // Al detectar un cambio (ej. producto guardado), limpiamos y recargamos
                _state.update { it.copy(products = emptyList(), filteredProducts = emptyList()) }
                loadStock() 
            }
            .launchIn(viewModelScope)
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
            
            getProductsUseCase().onSuccess { domainProducts ->
                val uiProducts = domainProducts.map { p ->
                    Product(
                        id = p.id ?: "",
                        name = p.name,
                        sku = p.sku ?: "S/SKU",
                        category = p.category?.name ?: "Sin categoría",
                        quantity = p.currentStock,
                        price = p.salePrice,
                        colorHex = "#3B82F6" // Opcional: podrías guardarlo en el modelo si quieres
                    )
                }
                
                _state.update {
                    it.copy(
                        isLoading = false,
                        products = uiProducts,
                        filteredProducts = uiProducts,
                        totalProducts = uiProducts.size,
                        lowStockCount = uiProducts.count { it.stockStatus == StockStatus.LOW },
                        noStockCount = uiProducts.count { it.stockStatus == StockStatus.NONE },
                    )
                }
                filterAndSearch()
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun applyFilter(filter: StockFilter) {
        _state.update { it.copy(selectedFilter = filter) }
        filterAndSearch()
    }

    private fun applySearch(query: String) {
        _state.update { it.copy(searchQuery = query) }
        filterAndSearch()
    }

    private fun filterAndSearch() {
        _state.update { state ->
            val filteredByStatus = when (state.selectedFilter) {
                StockFilter.ALL -> state.products
                StockFilter.LOW_STOCK -> state.products.filter { it.stockStatus == StockStatus.LOW }
                StockFilter.NO_STOCK -> state.products.filter { it.stockStatus == StockStatus.NONE }
            }

            val filteredBySearch = if (state.searchQuery.isBlank()) {
                filteredByStatus
            } else {
                filteredByStatus.filter {
                    it.name.contains(state.searchQuery, ignoreCase = true) ||
                            it.sku.contains(state.searchQuery, ignoreCase = true)
                }
            }

            state.copy(filteredProducts = filteredBySearch)
        }
    }

    private fun navigateToDetail(productId: String) {
        emitNav(StockNavigationEvent.NavigateToProductDetail(productId))
    }

    private fun emitNav(event: StockNavigationEvent) {
        viewModelScope.launch { _navigationEvent.send(event) }
    }
}