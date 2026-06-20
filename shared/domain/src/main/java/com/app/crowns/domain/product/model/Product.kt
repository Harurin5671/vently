package com.app.crowns.domain.product.model

import com.app.crowns.domain.catalog.entities.IgvTypeEntity

data class Product(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val sku: String? = null,
    val category: Category? = null,
    val salePrice: Double,
    val costPrice: Double,
    val igvType: IgvTypeEntity,
    val unitOfMeasure: String,
    val trackStock: Boolean,
    val currentStock: Int,
    val minStockAlert: Int? = null,
    val imageUrl: String? = null,
)

val Product.profitMargin: Double
    get() {
        if (costPrice == 0.0) return 0.0
        return ((salePrice - costPrice) / salePrice) * 100
    }
