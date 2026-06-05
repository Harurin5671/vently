package com.app.crowns.data.product.datasource

import com.app.crowns.data.product.dto.CategoryEntity
import com.app.crowns.data.product.dto.ProductEntity

interface ProductRemoteDataSource {
    suspend fun getProducts(companyId: String): Result<List<ProductEntity>>
    suspend fun saveProduct(product: ProductEntity): Result<ProductEntity>
    suspend fun updateProduct(product: ProductEntity): Result<Unit>
    suspend fun getCategories(companyId: String): Result<List<CategoryEntity>>
    suspend fun saveCategory(category: CategoryEntity): Result<Unit>
    suspend fun updateCategory(category: CategoryEntity): Result<Unit>
    suspend fun uploadProductImage(imageBytes: ByteArray, path: String): Result<String>
}
