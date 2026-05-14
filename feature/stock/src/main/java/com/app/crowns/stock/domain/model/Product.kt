package com.app.crowns.stock.domain.model

data class Product(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val sku: String? = null,
    val category: Category? = null,
    val salePrice: Double,
    val costPrice: Double? = null,
    val igvType: IgvType,
    val unitOfMeasure: String,
    val trackStock: Boolean,
    val currentStock: Int,
    val minStockAlert: Int? = null,
)

val Product.profitMargin: Double
    get() {
        val cost = costPrice ?: return 0.0
        if (cost == 0.0) return 0.0
        return ((salePrice - cost) / salePrice) * 100
    }