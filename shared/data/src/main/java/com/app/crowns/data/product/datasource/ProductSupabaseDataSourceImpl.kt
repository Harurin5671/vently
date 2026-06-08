package com.app.crowns.data.product.datasource

import com.app.crowns.data.product.dto.CategoryEntity
import com.app.crowns.data.product.dto.ProductEntity
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import javax.inject.Inject

class ProductSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : ProductRemoteDataSource {

    override suspend fun getProducts(companyId: String): Result<List<ProductEntity>> = runCatching {
        supabase.from("products")
            .select { filter { eq("company_id", companyId) } }
            .decodeList<ProductEntity>()
    }

    override suspend fun getProductById(productId: String): Result<ProductEntity> = runCatching {
        supabase.from("products")
            .select { filter { eq("id", productId) } }
            .decodeSingle<ProductEntity>()
    }

    override suspend fun saveProduct(product: ProductEntity): Result<ProductEntity> = runCatching {
        supabase.from("products")
            .insert(product) {
                select()
            }
            .decodeSingle<ProductEntity>()
    }

    override suspend fun updateProduct(product: ProductEntity): Result<Unit> = runCatching {
        supabase.from("products")
            .update(product) {
                filter { eq("id", product.id!!) }
            }
    }

    override suspend fun getCategories(companyId: String): Result<List<CategoryEntity>> = runCatching {
        supabase.from("categories")
            .select { filter { eq("company_id", companyId) } }
            .decodeList<CategoryEntity>()
    }

    override suspend fun saveCategory(category: CategoryEntity): Result<Unit> = runCatching {
        supabase.from("categories").insert(category)
    }

    override suspend fun updateCategory(category: CategoryEntity): Result<Unit> = runCatching {
        supabase.from("categories")
            .update(category) {
                filter { eq("id", category.id!!) }
            }
    }

    override suspend fun uploadProductImage(
        imageBytes: ByteArray,
        path: String
    ): Result<String> = runCatching {
        supabase.storage
            .from("product-images")
            .upload(path, imageBytes) {
                upsert = true
            }

        supabase.storage
            .from("product-images")
            .publicUrl(path)
    }
}
