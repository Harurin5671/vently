package com.app.crowns.data.product.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductEntity(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String? = null,
    @SerialName("sku") val sku: String? = null,
    @SerialName("category_id") val categoryId: String? = null,
    @SerialName("sale_price") val salePrice: Double,
    @SerialName("cost_price") val costPrice: Double? = null,
    @SerialName("igv_type") val igvType: String,
    @SerialName("unit_of_measure") val unitOfMeasure: String,
    @SerialName("track_stock") val trackStock: Boolean,
    @SerialName("current_stock") val currentStock: Int,
    @SerialName("min_stock_alert") val minStockAlert: Int? = null,
    @SerialName("created_at") val createdAt: String? = null,
)

@Serializable
data class CategoryEntity(
    @SerialName("id") val id: String? = null,
    @SerialName("user_id") val userId: String? = null,
    @SerialName("name") val name: String,
    @SerialName("created_at") val createdAt: String? = null,
)
