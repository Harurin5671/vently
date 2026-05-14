package com.app.crowns.stock.data.mapper

import com.app.crowns.stock.data.dto.CategoryEntity
import com.app.crowns.stock.data.dto.ProductEntity
import com.app.crowns.stock.domain.model.Category
import com.app.crowns.stock.domain.model.IgvType
import com.app.crowns.stock.domain.model.Product

fun ProductEntity.toDomain(category: Category? = null): Product = Product(
    id = id,
    name = name,
    description = description,
    sku = sku,
    category = category,
    salePrice = salePrice,
    costPrice = costPrice,
    igvType = IgvType.entries.find { it.code == igvType } ?: IgvType.GRAVADO,
    unitOfMeasure = unitOfMeasure,
    trackStock = trackStock,
    currentStock = currentStock,
    minStockAlert = minStockAlert,
)

fun Product.toEntity(userId: String): ProductEntity = ProductEntity(
    id = id,
    userId = userId,
    name = name,
    description = description,
    sku = sku,
    categoryId = category?.id,
    salePrice = salePrice,
    costPrice = costPrice,
    igvType = igvType.code,
    unitOfMeasure = unitOfMeasure,
    trackStock = trackStock,
    currentStock = currentStock,
    minStockAlert = minStockAlert,
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id ?: "",
    name = name,
)

fun Category.toEntity(userId: String): CategoryEntity = CategoryEntity(
    id = id.ifEmpty { null },
    userId = userId,
    name = name,
)