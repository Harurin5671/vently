package com.app.crowns.data.product.datasource

import com.app.crowns.data.product.dto.CategoryEntity
import com.app.crowns.data.product.dto.ProductEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class ProductSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : ProductRemoteDataSource {

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
