package com.app.crowns.data.product.repository

import com.app.crowns.data.product.datasource.ProductRemoteDataSource
import com.app.crowns.data.product.mapper.toDomain
import com.app.crowns.data.product.mapper.toEntity
import com.app.crowns.domain.product.model.Category
import com.app.crowns.domain.product.model.Product
import com.app.crowns.domain.product.repository.ProductRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val remoteDataSource: ProductRemoteDataSource,
    private val supabase: SupabaseClient,
) : ProductRepository {

    private val _productChanges = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val productChanges = _productChanges.asSharedFlow()

    override suspend fun notifyProductChanged() {
        _productChanges.emit(Unit)
    }

    override suspend fun getProducts(): Result<List<Product>> = runCatching {
        val categories = remoteDataSource.getCategories().getOrDefault(emptyList())
        remoteDataSource.getProducts().getOrThrow().map { entity ->
            val category = categories.find { it.id == entity.categoryId }?.toDomain()
            entity.toDomain(category)
        }
    }

    override suspend fun saveProduct(product: Product): Result<Unit> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("User not authenticated"))
        return remoteDataSource.saveProduct(product.toEntity(userId)).onSuccess {
            notifyProductChanged()
        }
    }

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        remoteDataSource.getCategories().getOrThrow().map { it.toDomain() }
    }

    override suspend fun saveCategory(category: Category): Result<Unit> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("User not authenticated"))
        return remoteDataSource.saveCategory(category.toEntity(userId)).onSuccess {
            notifyProductChanged()
        }
    }
}
