package com.app.crowns.stock.ui.addproduct

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.stock.domain.model.Category
import com.app.crowns.stock.domain.model.IgvType
import com.app.crowns.stock.domain.model.Product
import com.app.crowns.stock.domain.usecase.GetCategoriesUseCase
import com.app.crowns.stock.domain.usecase.SaveProductUseCase
import com.app.crowns.stock.domain.repository.StockRepository
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
class AddProductViewModel @Inject constructor(
    private val getCategories: GetCategoriesUseCase,
    private val saveProduct: SaveProductUseCase,
    private val repository: StockRepository,
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductUiState())
    val state: StateFlow<AddProductUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<AddProductNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadCategories()
    }

    fun onEvent(event: AddProductEvent) {
        when (event) {
            is AddProductEvent.OnNameChanged -> _state.update { it.copy(name = event.value) }
            is AddProductEvent.OnDescriptionChanged -> _state.update { it.copy(description = event.value) }
            is AddProductEvent.OnSkuChanged -> _state.update { it.copy(sku = event.value) }
            is AddProductEvent.OnCategorySelected -> _state.update { it.copy(selectedCategory = event.category) }
            is AddProductEvent.OnSalePriceChanged -> _state.update { it.copy(salePrice = event.value) }
            is AddProductEvent.OnCostPriceChanged -> _state.update { it.copy(costPrice = event.value) }
            is AddProductEvent.OnIgvTypeSelected -> _state.update { it.copy(igvType = event.igvType) }
            is AddProductEvent.OnUnitOfMeasureSelected -> _state.update { it.copy(unitOfMeasure = event.unit) }
            is AddProductEvent.OnTrackStockChanged -> _state.update { it.copy(trackStock = event.value) }
            is AddProductEvent.OnCurrentStockChanged -> _state.update { it.copy(currentStock = event.value) }
            is AddProductEvent.OnMinStockAlertChanged -> _state.update { it.copy(minStockAlert = event.value) }
            is AddProductEvent.OnNewCategoryNameChanged -> _state.update { it.copy(newCategoryName = event.value) }
            is AddProductEvent.OnShowAddCategoryDialog -> _state.update { it.copy(showAddCategoryDialog = true) }
            is AddProductEvent.OnDismissAddCategoryDialog -> _state.update { it.copy(showAddCategoryDialog = false, newCategoryName = "") }
            is AddProductEvent.OnSaveCategory -> saveCategory()
            is AddProductEvent.OnSaveProduct -> saveProductData()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategories().onSuccess { categories ->
                _state.update { it.copy(categories = categories) }
            }
        }
    }

    private fun saveCategory() {
        val name = _state.value.newCategoryName.trim()
        if (name.isBlank()) return
        viewModelScope.launch {
            repository.saveCategory(Category(id = "", name = name))
                .onSuccess {
                    loadCategories()
                    _state.update { it.copy(showAddCategoryDialog = false, newCategoryName = "") }
                }
                .onFailure { error ->
                    _state.update { it.copy(error = error.message) }
                }
        }
    }

    private fun saveProductData() {
        val s = _state.value
        if (!s.isFormValid) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val product = Product(
                name = s.name,
                description = s.description.ifBlank { null },
                sku = s.sku.ifBlank { null },
                category = s.selectedCategory,
                salePrice = s.salePrice.toDouble(),
                costPrice = s.costPrice.toDoubleOrNull(),
                igvType = s.igvType,
                unitOfMeasure = s.unitOfMeasure,
                trackStock = s.trackStock,
                currentStock = s.currentStock,
                minStockAlert = s.minStockAlert.toIntOrNull(),
            )
            saveProduct(product)
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _navigationEvent.send(AddProductNavigationEvent.NavigateBack)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSaving = false, error = error.message) }
                }
        }
    }
}