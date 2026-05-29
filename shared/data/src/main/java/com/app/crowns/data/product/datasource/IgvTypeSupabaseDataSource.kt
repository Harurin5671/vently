package com.app.crowns.data.product.datasource

import com.app.crowns.data.product.model.IgvTypeSupabaseModel

interface IgvTypeSupabaseDataSource {
    suspend fun getIgvTypes(): Result<List<IgvTypeSupabaseModel>>
}