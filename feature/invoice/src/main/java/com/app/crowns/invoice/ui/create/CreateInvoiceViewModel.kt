package com.app.crowns.invoice.ui.create

import com.app.crowns.domain.company.usecase.GetCompanyUseCase
import com.app.crowns.domain.company.usecase.SearchCompanyUseCase
import com.app.crowns.domain.customer.usecase.SearchPersonUseCase
import com.app.crowns.domain.catalog.usecase.GetIdentityDocumentTypesUseCase
import com.app.crowns.domain.catalog.entities.IdentityDocumentTypeEntity
import com.app.crowns.invoice.domain.model.Invoice
import com.app.crowns.invoice.domain.model.InvoiceItem
import com.app.crowns.invoice.domain.model.InvoiceType
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
    private val searchPersonUseCase: SearchPersonUseCase,
    private val getCompanyUseCase: GetCompanyUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getIdentityDocumentTypes: GetIdentityDocumentTypesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(CreateInvoiceUiState())
    val state: StateFlow<CreateInvoiceUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<CreateInvoiceNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    private var myCompanyRuc: String? = null

    init {
        loadProducts()
        loadCompanyRuc()
        loadDocumentTypes()
    }

    private fun loadDocumentTypes() {
        viewModelScope.launch {
            getIdentityDocumentTypes().onSuccess { types ->
                _state.update { 
                    val targetType = if (it.invoiceType == InvoiceType.FACTURA) 
                        IdentityDocumentTypeEntity.Type.RUC 
                    else 
                        IdentityDocumentTypeEntity.Type.DNI
                    
                    it.copy(
                        documentTypes = types,
                        selectedDocumentType = types.find { t -> t.type == targetType } ?: types.firstOrNull()
                    )
                }
            }
        }
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
                val newDocType = if (event.type == InvoiceType.FACTURA) {
                    it.documentTypes.find { t -> t.isRuc }
                } else {
                    it.documentTypes.find { t -> t.isDni } ?: it.documentTypes.firstOrNull()
                }
                it.copy(
                    invoiceType = event.type,
                    selectedDocumentType = newDocType,
                    clientName = "",
                    clientRuc = "",
                    clientBusinessName = "",
                    rucError = null,
                )
            }

            is CreateInvoiceEvent.OnDocumentTypeChanged -> _state.update {
                it.copy(
                    selectedDocumentType = event.type,
                    clientRuc = "", // Clear number when type changes to avoid length conflicts
                    rucError = null,
                    clientName = "",
                    clientBusinessName = ""
                )
            }

            is CreateInvoiceEvent.OnClientNameChanged -> _state.update {
                if (it.selectedDocumentType?.isRuc == true) {
                    it.copy(clientBusinessName = event.value)
                } else {
                    it.copy(clientName = event.value)
                }
            }

            is CreateInvoiceEvent.OnClientRucChanged -> {
                val state = _state.value
                val maxLength = if (state.invoiceType == InvoiceType.FACTURA) 11 
                                else state.selectedDocumentType?.maxLength?.toInt() ?: 15
                
                val newValue = event.value.filter { it.isDigit() }.take(maxLength)
                
                var error: String? = null
                if (state.invoiceType == InvoiceType.FACTURA && newValue.length == 11) {
                    if (!newValue.startsWith("10") && !newValue.startsWith("20")) {
                        error = "El RUC debe empezar con 10 o 20"
                    }
                }

                _state.update {
                    it.copy(clientRuc = newValue, rucError = error, clientBusinessName = "", clientName = "")
                }
            }

            is CreateInvoiceEvent.OnSearchRuc -> {
                val state = _state.value
                if (state.selectedDocumentType?.isDni == true) {
                    searchDni()
                } else {
                    searchRuc()
                }
            }
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

            is CreateInvoiceEvent.RefreshProducts -> loadProducts()

            is CreateInvoiceEvent.OnPreview -> onPreview()
        }
    }

    private fun searchDni() {
        val dni = _state.value.clientRuc
        if (dni.length != 8) {
            _state.update { it.copy(rucError = "El DNI debe tener 8 dígitos") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isSearchingRuc = true, rucError = null) }
            searchPersonUseCase(dni).fold(
                onSuccess = { person ->
                    _state.update {
                        it.copy(
                            isSearchingRuc = false,
                            clientName = person.fullName,
                        )
                    }
                },
                onHttpError = { error ->
                    _state.update {
                        it.copy(
                            isSearchingRuc = false,
                            rucError = "No se encontró el DNI o hubo un error"
                        )
                    }
                },
                onNetworkError = {
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
            clientDocumentType = s.selectedDocumentType?.code,
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