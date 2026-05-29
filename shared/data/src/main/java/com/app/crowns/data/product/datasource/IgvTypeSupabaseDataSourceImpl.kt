package com.app.crowns.data.product.datasource

import com.app.crowns.data.product.model.IgvTypeSupabaseModel
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class IgvTypeSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient,
): IgvTypeSupabaseDataSource {
    override suspend fun getIgvTypes(): Result<List<IgvTypeSupabaseModel>> = runCatching {
        supabase
            .from("cat_igv_types")
            .select()
            .decodeList<IgvTypeSupabaseModel>()
    }
}