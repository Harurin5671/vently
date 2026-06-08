package com.app.crowns.stock.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.catalog.usecase.GetIgvTypesUseCase
import com.app.crowns.domain.catalog.usecase.GetUnitsOfMeasureUseCase
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.usecase.GetProductsUseCase
import com.app.crowns.domain.product.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.app.crowns.common.util.toUserFriendlyMessage
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
    private val getIgvTypes: GetIgvTypesUseCase,
    private val getUnitsOfMeasure: GetUnitsOfMeasureUseCase,
    repository: ProductRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(StockUiState())
    val state: StateFlow<StockUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<StockNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadIgvTypes()
        loadUnitsOfMeasure()
        loadStock()

        repository.productChanges
            .onEach {
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
            is StockEvent.OnProductSelected -> {
                val resolved = resolveIgvType(event.product)
                _state.update { it.copy(selectedProduct = resolved) }
            }
            is StockEvent.OnDismissProductDetail -> _state.update { it.copy(selectedProduct = null, isNavigatingToEdit = false) }
            is StockEvent.OnEditProduct -> {
                viewModelScope.launch {
                    _state.update { it.copy(isNavigatingToEdit = true) }
                    // Pequeño delay para que el usuario vea el estado de carga en el botón
                    kotlinx.coroutines.delay(200)
                    _state.update { it.copy(selectedProduct = null, isNavigatingToEdit = false) }
                    emitNav(StockNavigationEvent.NavigateToEditProduct(event.productId))
                }
            }
            is StockEvent.OnAddProduct -> emitNav(StockNavigationEvent.NavigateToAddProduct)
        }
    }

    private fun loadStock() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            getProductsUseCase().onSuccess { domainProducts ->
//                val uiProducts = domainProducts.map { p ->
//                    Product(
//                        id = p.id ?: "",
//                        name = p.name,
//                        sku = p.sku ?: "S/SKU",
//                        category = p.category?.name ?: "Sin categoría",
//                        quantity = p.currentStock,
//                        price = p.salePrice,
//                        colorHex = "#3B82F6"
//                    )
//                }

                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        products = domainProducts,
                        filteredProducts = domainProducts,
                        totalProducts = domainProducts.size,
                        lowStockCount = domainProducts.count { it.stockStatus == StockStatus.LOW },
                        noStockCount = domainProducts.count { it.stockStatus == StockStatus.NONE },
                    )
                }
                filterAndSearch()
            }.onFailure { e ->
                _state.update { it.copy(isLoading = false, error = e.toUserFriendlyMessage("Error al cargar productos")) }
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
                            (it.sku ?: "").contains(state.searchQuery, ignoreCase = true)
                }
            }

            state.copy(filteredProducts = filteredBySearch)
        }
    }

    private fun navigateToDetail(productId: String) {
        emitNav(StockNavigationEvent.NavigateToProductDetail(productId))
    }

    private fun loadIgvTypes() {
        viewModelScope.launch {
            getIgvTypes().onSuccess { types ->
                _state.update { it.copy(igvTypes = types) }
            }
        }
    }

    private fun loadUnitsOfMeasure() {
        viewModelScope.launch {
            getUnitsOfMeasure().onSuccess { units ->
                _state.update { it.copy(unitsOfMeasure = units) }
            }
        }
    }

    private fun resolveIgvType(product: Product): Product {
        val types = _state.value.igvTypes
        if (types.isEmpty()) return product
        val matched = types.find { it.code == product.igvType.code } ?: return product
        return product.copy(igvType = matched)
    }

    private fun emitNav(event: StockNavigationEvent) {
        viewModelScope.launch { _navigationEvent.send(event) }
    }
}