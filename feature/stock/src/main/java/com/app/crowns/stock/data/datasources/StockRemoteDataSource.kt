package com.app.crowns.stock.data.datasource

import com.app.crowns.stock.data.dto.CategoryEntity
import com.app.crowns.stock.data.dto.ProductEntity

interface StockRemoteDataSource {
    suspend fun getProducts(): Result<List<ProductEntity>>
    suspend fun saveProduct(product: ProductEntity): Result<Unit>
    suspend fun getCategories(): Result<List<CategoryEntity>>
    suspend fun saveCategory(category: CategoryEntity): Result<Unit>
}