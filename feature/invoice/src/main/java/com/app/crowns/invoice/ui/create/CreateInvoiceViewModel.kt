package com.app.crowns.invoice.ui.create

import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.company.usecase.SearchCompanyUseCase
import com.app.crowns.invoice.domain.model.Invoice
import com.app.crowns.invoice.domain.model.InvoiceItem
import com.app.crowns.invoice.domain.store.InvoiceDraftStore
import com.app.crowns.network.fold
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.product.usecase.GetCategoriesUseCase
import com.app.crowns.domain.product.usecase.GetProductsUseCase
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
class CreateInvoiceViewModel @Inject constructor(
    private val draftStore: InvoiceDraftStore,
    private val searchCompanyUseCase: SearchCompanyUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CreateInvoiceUiState())
    val state: StateFlow<CreateInvoiceUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<CreateInvoiceNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var myCompanyRuc: String? = null

    init {
        loadProducts()
        loadCompanyRuc()
    }

    private fun loadCompanyRuc() {
        viewModelScope.launch {
            getCompanyUseCase().onSuccess {
                myCompanyRuc = it?.documentNumber
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            _state.update { it.copy(isLoadingProducts = true) }
            val productsResult = getProductsUseCase()
            val categoriesResult = getCategoriesUseCase()

            _state.update {
                it.copy(
                    isLoadingProducts = false,
                    availableProducts = productsResult.getOrDefault(emptyList()),
                    categories = categoriesResult.getOrDefault(emptyList())
                )
            }
        }
    }

    fun onEvent(event: CreateInvoiceEvent) {
        when (event) {
            is CreateInvoiceEvent.OnInvoiceTypeChanged -> _state.update {
                it.copy(
                    invoiceType = event.type,
                    clientName = "",
                    clientRuc = "",
                    clientBusinessName = "",
                    rucError = null,
                )
            }

            is CreateInvoiceEvent.OnClientNameChanged -> _state.update {
                it.copy(clientName = event.value)
            }

            is CreateInvoiceEvent.OnClientRucChanged -> {
                val newValue = event.value.filter { it.isDigit() }.take(11)
                _state.update {
                    it.copy(clientRuc = newValue, rucError = null, clientBusinessName = "")
                }
            }

            is CreateInvoiceEvent.OnSearchRuc -> searchRuc()
            is CreateInvoiceEvent.OnAddItem -> addItem(event.item)
            is CreateInvoiceEvent.OnRemoveItem -> removeItem(event.productId)
            is CreateInvoiceEvent.OnIncreaseQuantity -> changeQuantity(event.productId, +1)
            is CreateInvoiceEvent.OnDecreaseQuantity -> changeQuantity(event.productId, -1)
            is CreateInvoiceEvent.OnShowAddProductSheet -> _state.update {
                it.copy(showAddProductSheet = true)
            }

            is CreateInvoiceEvent.OnDismissAddProductSheet -> _state.update {
                it.copy(showAddProductSheet = false, productSearchQuery = "")
            }

            is CreateInvoiceEvent.OnProductSearchChanged -> _state.update {
                it.copy(productSearchQuery = event.query)
            }

            is CreateInvoiceEvent.OnCategoryFilterChanged -> _state.update {
                it.copy(selectedCategoryFilter = event.category)
            }

            is CreateInvoiceEvent.OnPreview -> onPreview()
        }
    }

    private fun searchRuc() {
        val ruc = _state.value.clientRuc
        android.util.Log.d("SEARCH_RUC_VM", "searchRuc called with: $ruc")
        if (ruc.length != 11) {
            _state.update { it.copy(rucError = "El RUC debe tener 11 dígitos") }
            return
        }

        if (ruc == myCompanyRuc) {
            _state.update { it.copy(rucError = "No puedes emitir una factura a tu propio RUC") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSearchingRuc = true, rucError = null) }
            searchCompanyUseCase(ruc).fold(
                onSuccess = { company ->
                    android.util.Log.d("SEARCH_RUC_VM", "Success: $company")
                    _state.update {
                        it.copy(
                            isSearchingRuc = false,
                            clientBusinessName = company.businessName,
                        )
                    }
                },
                onHttpError = { error ->
                    android.util.Log.e("SEARCH_RUC_VM", "HTTP Error: $error")
                    _state.update {
                        it.copy(
                            isSearchingRuc = false,
                            rucError = "No se encontró el RUC o hubo un error"
                        )
                    }
                },
                onNetworkError = {
                    android.util.Log.e("SEARCH_RUC_VM", "Network Error")
                    _state.update {
                        it.copy(
                            isSearchingRuc = false,
                            rucError = "Sin conexión a internet"
                        )
                    }
                }
            )
        }
    }

    private fun addItem(item: InvoiceItem) {
        _state.update { state ->
            val existing = state.items.find { it.productId == item.productId }
            if (existing != null) {
                state.copy(
                    items = state.items.map {
                        if (it.productId == item.productId) it.copy(quantity = it.quantity + 1)
                        else it
                    },
                    showAddProductSheet = false,
                )
            } else {
                state.copy(
                    items = state.items + item,
                    showAddProductSheet = false,
                )
            }
        }
    }

    private fun removeItem(productId: String) {
        _state.update { it.copy(items = it.items.filter { item -> item.productId != productId }) }
    }

    private fun changeQuantity(productId: String, delta: Int) {
        _state.update { state ->
            state.copy(
                items = state.items.mapNotNull { item ->
                    if (item.productId == productId) {
                        val newQty = item.quantity + delta
                        if (newQty <= 0) null else item.copy(quantity = newQty)
                    } else item
                }
            )
        }
    }

    private fun onPreview() {
        val s = _state.value
        val invoice = Invoice(
            type = s.invoiceType,
            clientName = s.clientName.ifBlank { null },
            clientRuc = s.clientRuc.ifBlank { null },
            clientBusinessName = s.clientBusinessName.ifBlank { null },
            items = s.items,
        )
        draftStore.save(invoice)
        viewModelScope.launch {
            _navigationEvent.send(CreateInvoiceNavigationEvent.NavigateToPreview)
        }
    }
}