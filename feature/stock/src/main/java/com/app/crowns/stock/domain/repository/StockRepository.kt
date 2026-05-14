package com.app.crowns.stock.domain.repository

import com.app.crowns.stock.domain.model.Category
import com.app.crowns.stock.domain.model.Product

interface StockRepository {
    suspend fun getProducts(): Result<List<Product>>
    suspend fun saveProduct(product: Product): Result<Unit>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun saveCategory(category: Category): Result<Unit>
}