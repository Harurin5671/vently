package com.app.crowns.stock.ui.addproduct

import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.IgvType

data class AddProductUiState(
    val name: String = "",
    val description: String = "",
    val sku: String = "",
    val selectedCategory: Category? = null,
    val categories: List<Category> = emptyList(),
    val salePrice: String = "",
    val costPrice: String = "",
    val igvType: IgvType = IgvType.GRAVADO,
    val unitOfMeasure: String = "NIU",
    val trackStock: Boolean = true,
    val currentStock: Int = 0,
    val minStockAlert: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
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

val AddProductUiState.isFormValid: Boolean
    get() = name.isNotBlank() && salePrice.toDoubleOrNull() != null

val unitOfMeasureOptions = listOf(
    "NIU" to "NIU - Unidades",
    "KGM" to "KGM - Kilogramos",
    "MTR" to "MTR - Metros",
    "LTR" to "LTR - Litros",
    "GLL" to "GLL - Galones",
)