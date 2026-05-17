package com.app.crowns.data.product.datasource

import com.app.crowns.data.product.dto.CategoryEntity
import com.app.crowns.data.product.dto.ProductEntity

interface ProductRemoteDataSource {
    suspend fun getProducts(): Result<List<ProductEntity>>
    suspend fun saveProduct(product: ProductEntity): Result<Unit>
    suspend fun getCategories(): Result<List<CategoryEntity>>
    suspend fun saveCategory(category: CategoryEntity): Result<Unit>
}
