package com.app.crowns.stock.ui.addproduct

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import com.app.crowns.domain.product.usecase.GetCategoriesUseCase
import com.app.crowns.domain.catalog.usecase.GetIgvTypesUseCase
import com.app.crowns.domain.catalog.usecase.GetUnitsOfMeasureUseCase
import com.app.crowns.domain.product.usecase.SaveProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val getIgvTypes: GetIgvTypesUseCase,
    private val getUnitsOfMeasure: GetUnitsOfMeasureUseCase,
    private val saveProduct: SaveProductUseCase,
    private val repository: ProductRepository,
    @param:ApplicationContext private val context: Context,
) : ViewModel() {

    private val _state = MutableStateFlow(AddProductUiState())
    val state: StateFlow<AddProductUiState> = _state.asStateFlow()

    private val _navigationEvent = Channel<AddProductNavigationEvent>(Channel.BUFFERED)
    val navigationEvent = _navigationEvent.receiveAsFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            loadCategories()
            loadIgvTypes()
            loadUnitsOfMeasure()
        }
    }

    fun onEvent(event: AddProductEvent) {
        when (event) {
            is AddProductEvent.OnImageSelected -> _state.update { it.copy(imageUri = event.uri) }
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

    private suspend fun loadCategories() {
        getCategories().onSuccess { categories ->
            _state.update { it.copy(categories = categories) }
        }
    }

    private suspend fun loadIgvTypes() {
        getIgvTypes().onSuccess { types ->
            _state.update { 
                it.copy(igvTypes = types)
            }
        }
    }

    private suspend fun loadUnitsOfMeasure() {
        getUnitsOfMeasure().onSuccess { units ->
            _state.update { it.copy(unitsOfMeasure = units) }
        }
    }

    private fun saveCategory() {
        val name = _state.value.newCategoryName.trim()
        Log.d("AddProductViewModel", "saveCategory -> name: $name")
        if (name.isBlank()) {
            Log.w("AddProductViewModel", "saveCategory -> name is blank, returning")
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            repository.saveCategory(Category(id = "", name = name))
                .onSuccess {
                    Log.d("AddProductViewModel", "saveCategory -> Success")
                    loadCategories()
                    _state.update { it.copy(showAddCategoryDialog = false, newCategoryName = "", isSaving = false) }
                }
                .onFailure { error ->
                    Log.e("AddProductViewModel", "saveCategory -> Error: ${error.message}", error)
                    _state.update { it.copy(error = error.message, isSaving = false) }
                }
        }
    }

    private fun saveProductData() {
        val s = _state.value
        val igvType = s.igvType ?: return
        if (!s.isFormValid) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            
            val imageBytes = s.imageUri?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    Log.e("AddProductViewModel", "Error reading image bytes", e)
                    null
                }
            }

            val product = Product(
                name = s.name,
                description = s.description.ifBlank { null },
                sku = s.sku.ifEmpty { null }?.let { 
                    if (it.length == 6) "${it.take(3)}-${it.drop(3)}" else it 
                },
                category = s.selectedCategory,
                salePrice = s.salePrice.toDouble(),
                costPrice = s.costPrice.toDoubleOrNull(),
                igvType = igvType,
                unitOfMeasure = s.unitOfMeasure,
                trackStock = s.trackStock,
                currentStock = s.currentStock,
                minStockAlert = s.minStockAlert.toIntOrNull(),
            )
            Log.d("AddProductViewModel", "product -> name: $product")
            saveProduct(product, imageBytes)
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
