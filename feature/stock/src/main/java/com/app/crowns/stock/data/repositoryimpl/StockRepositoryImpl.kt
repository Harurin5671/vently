package com.app.crowns.stock.data.repositoryimpl

import com.app.crowns.stock.data.datasource.StockRemoteDataSource
import com.app.crowns.stock.data.mapper.toDomain
import com.app.crowns.stock.data.mapper.toEntity
import com.app.crowns.stock.domain.model.Category
import com.app.crowns.stock.domain.model.Product
import com.app.crowns.stock.domain.repository.StockRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepositoryImpl @Inject constructor(
    private val remoteDataSource: StockRemoteDataSource,
    private val supabase: SupabaseClient,
) : StockRepository {

    private val _stockChanges = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    override val stockChanges = _stockChanges.asSharedFlow()

    override suspend fun notifyStockChanged() {
        _stockChanges.emit(Unit)
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
            notifyStockChanged()
        }
    }

    override suspend fun getCategories(): Result<List<Category>> = runCatching {
        remoteDataSource.getCategories().getOrThrow().map { it.toDomain() }
    }

    override suspend fun saveCategory(category: Category): Result<Unit> {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.failure(Exception("User not authenticated"))
        return remoteDataSource.saveCategory(category.toEntity(userId)).onSuccess {
            notifyStockChanged()
        }
    }
}