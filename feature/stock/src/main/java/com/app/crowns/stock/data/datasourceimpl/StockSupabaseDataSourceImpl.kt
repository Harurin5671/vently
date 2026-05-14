package com.app.crowns.stock.data.datasourceimpl

import com.app.crowns.stock.data.datasource.StockRemoteDataSource
import com.app.crowns.stock.data.dto.CategoryEntity
import com.app.crowns.stock.data.dto.ProductEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class StockSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : StockRemoteDataSource {

    override suspend fun getProducts(): Result<List<ProductEntity>> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")
        supabase.from("products")
            .select { filter { eq("user_id", userId) } }
            .decodeList<ProductEntity>()
    }

    override suspend fun saveProduct(product: ProductEntity): Result<Unit> = runCatching {
        supabase.from("products").insert(product)
    }

    override suspend fun getCategories(): Result<List<CategoryEntity>> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")
        supabase.from("categories")
            .select { filter { eq("user_id", userId) } }
            .decodeList<CategoryEntity>()
    }

    override suspend fun saveCategory(category: CategoryEntity): Result<Unit> = runCatching {
        supabase.from("categories").insert(category)
    }
}