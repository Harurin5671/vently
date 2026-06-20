package com.app.crowns.stock.ui.addproduct

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import com.app.crowns.domain.product.usecase.GetCategoriesUseCase
import com.app.crowns.domain.product.usecase.GetProductByIdUseCase
import com.app.crowns.domain.catalog.usecase.GetIgvTypesUseCase
import com.app.crowns.domain.catalog.usecase.GetUnitsOfMeasureUseCase
import com.app.crowns.domain.product.usecase.SaveProductUseCase
import com.app.crowns.common.util.toUserFriendlyMessage
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
    private val getProductById: GetProductByIdUseCase,
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
            is AddProductEvent.OnNewCategoryNameChanged -> _state.update { 
                it.copy(
                    newCategoryName = event.value,
                    error = if (it.showAddCategoryDialog) null else it.error
                ) 
            }
            is AddProductEvent.OnShowAddCategoryDialog -> _state.update { it.copy(showAddCategoryDialog = true) }
            is AddProductEvent.OnDismissAddCategoryDialog -> _state.update { it.copy(showAddCategoryDialog = false, newCategoryName = "", editingCategoryId = null, error = null) }
            is AddProductEvent.OnEditCategorySelected -> _state.update { it.copy(newCategoryName = event.category.name, editingCategoryId = event.category.id, error = null) }
            is AddProductEvent.OnSaveCategory -> saveCategory()
            is AddProductEvent.OnSaveProduct -> saveProductData()
            is AddProductEvent.OnDismissError -> _state.update { it.copy(error = null) }
            is AddProductEvent.OnLoadProductForEdit -> loadProductForEdit(event.productId)
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
        val editingId = _state.value.editingCategoryId
        if (name.isBlank()) return
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }
            val result = if (editingId != null) {
                repository.updateCategory(Category(id = editingId, name = name))
            } else {
                repository.saveCategory(Category(id = "", name = name))
            }
            result
                .onSuccess {
                    loadCategories()
                    _state.update { it.copy(showAddCategoryDialog = false, newCategoryName = "", editingCategoryId = null, isSaving = false) }
                }
                .onFailure { error ->
                    val label = if (editingId != null) "Error al actualizar categoría" else "Error al guardar categoría"
                    _state.update { it.copy(error = error.toUserFriendlyMessage(label), isSaving = false) }
                }
        }
    }

    private fun loadProductForEdit(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // Ensure catalog data is loaded before pre-filling selections
            if (_state.value.igvTypes.isEmpty()) {
                loadIgvTypes()
                loadUnitsOfMeasure()
            }
            if (_state.value.categories.isEmpty()) {
                loadCategories()
            }

            getProductById(productId).onSuccess { product ->
                val skuRaw = product.sku ?: ""

                // Match against catalog list by code to ensure dropdown equality works
                val matchedIgvType = _state.value.igvTypes
                    .find { it.code == product.igvType.code }
                    ?: product.igvType
                val matchedCategory = _state.value.categories
                    .find { it.id == product.category?.id }
                    ?: product.category

                _state.update { state ->
                    state.copy(
                        isLoading = false,
                        editingProductId = productId,
                        existingImageUrl = product.imageUrl,
                        name = product.name,
                        description = product.description ?: "",
                        sku = skuRaw,
                        selectedCategory = matchedCategory,
                        salePrice = product.salePrice.toString(),
                        costPrice = product.costPrice.toString(),
                        igvType = matchedIgvType,
                        unitOfMeasure = product.unitOfMeasure,
                        trackStock = product.trackStock,
                        currentStock = product.currentStock,
                        minStockAlert = product.minStockAlert?.toString() ?: "",
                    )
                }
            }.onFailure {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun saveProductData() {
        val s = _state.value
        if (!s.isFormValid) {
            _state.update { it.copy(showErrors = true) }
            return
        }
        val igvType = s.igvType ?: return

        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, error = null) }

            val imageBytes = s.imageUri?.let { uri ->
                try {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                } catch (e: Exception) {
                    null
                }
            }

            val product = Product(
                id = s.editingProductId,
                name = s.name,
                description = s.description.ifBlank { null },
                sku = s.sku.ifEmpty { null },
                category = s.selectedCategory,
                salePrice = s.salePrice.toDouble(),
                costPrice = s.costPrice.toDouble(),
                igvType = igvType,
                unitOfMeasure = s.unitOfMeasure,
                trackStock = s.trackStock,
                currentStock = s.currentStock,
                minStockAlert = s.minStockAlert.toIntOrNull(),
                // Preserve existing image URL when no new image is selected
                imageUrl = if (imageBytes == null) s.existingImageUrl else null,
            )
            saveProduct(product, imageBytes)
                .onSuccess {
                    _state.update { it.copy(isSaving = false) }
                    _navigationEvent.send(AddProductNavigationEvent.NavigateBack)
                }
                .onFailure { error ->
                    val message = error.toUserFriendlyMessage("Error al guardar producto")
                    _state.update { it.copy(isSaving = false, error = message) }
                }
        }
    }
}
