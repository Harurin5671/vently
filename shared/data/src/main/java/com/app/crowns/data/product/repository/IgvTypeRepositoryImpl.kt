package com.app.crowns.data.product.repository

import com.app.crowns.data.product.datasource.IgvTypeSupabaseDataSource
import com.app.crowns.data.product.mapper.toDomain
import com.app.crowns.domain.product.entities.IgvTypeEntity
import com.app.crowns.domain.product.repository.IgvTypeRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class IgvTypeRepositoryImpl @Inject constructor(
    private val igvTypeSupabaseDataSource: IgvTypeSupabaseDataSource
): IgvTypeRepository {
    override suspend fun getIgvType(): Result<List<IgvTypeEntity>> = runCatching {
        igvTypeSupabaseDataSource.getIgvTypes().getOrThrow().map { model ->
            model.toDomain()
        }
    }
}