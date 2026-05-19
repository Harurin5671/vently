package com.app.crowns.data.product.mapper

import com.app.crowns.data.product.dto.CategoryEntity
import com.app.crowns.data.product.dto.ProductEntity
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.IgvType
import com.app.crowns.domain.product.model.Product

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
    imageUrl = imageUrl,
)

fun Product.toEntity(companyId: String): ProductEntity = ProductEntity(
    id = id,
    companyId = companyId,
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
    imageUrl = imageUrl,
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id ?: "",
    name = name,
)

fun Category.toEntity(companyId: String): CategoryEntity = CategoryEntity(
    id = id.ifEmpty { null },
    companyId = companyId,
    name = name,
)
