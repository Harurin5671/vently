package com.app.crowns.domain.product.repository

import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    val productChanges: Flow<Unit>
    suspend fun getProducts(): Result<List<Product>>
    suspend fun saveProduct(product: Product, imageBytes: ByteArray? = null): Result<Unit>
    suspend fun getCategories(): Result<List<Category>>
    suspend fun saveCategory(category: Category): Result<Unit>
    suspend fun updateCategory(category: Category): Result<Unit>
    suspend fun notifyProductChanged()
}
