package com.app.crowns.stock.ui.addproduct

import android.net.Uri
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.catalog.entities.IgvTypeEntity
import com.app.crowns.domain.catalog.entities.UnitOfMeasureEntity

data class AddProductUiState(
    val imageUri: Uri? = null,
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val salePrice: String = "",
    val costPrice: String = "",
    val igvType: IgvTypeEntity? = null,
    val igvTypes: List<IgvTypeEntity> = emptyList(),
    val unitOfMeasure: String = "",
    val unitsOfMeasure: List<UnitOfMeasureEntity> = emptyList(),
    val trackStock: Boolean = true,
    val currentStock: Int = 0,
    val minStockAlert: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val showErrors: Boolean = false,
    val showAddCategoryDialog: Boolean = false,
    val newCategoryName: String = "",
)

val AddProductUiState.profitMargin: Double
    get() {
        val sale = salePrice.toDoubleOrNull() ?: return 0.0
        val cost = costPrice.toDoubleOrNull() ?: return 0.0
        if (sale == 0.0) return 0.0
        return ((sale - cost) / sale) * 100
    }

val AddProductUiState.isSkuValid: Boolean
    get() = sku.isEmpty() || sku.matches(Regex("^[A-Z]{3}[0-9]{3}$"))

val AddProductUiState.isFormValid: Boolean
    get() = name.isNotBlank() &&
            selectedCategory != null &&
            salePrice.toDoubleOrNull() != null &&
            costPrice.toDoubleOrNull() != null &&
            unitOfMeasure.isNotBlank() &&
            igvType != null &&
            isSkuValid
