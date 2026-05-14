package com.app.crowns.stock.di

import com.app.crowns.stock.data.datasource.StockRemoteDataSource
import com.app.crowns.stock.data.datasourceimpl.StockSupabaseDataSourceImpl
import com.app.crowns.stock.data.repositoryimpl.StockRepositoryImpl
import com.app.crowns.stock.domain.repository.StockRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StockModule {

    @Binds
    @Singleton
    abstract fun bindStockRepository(
        impl: StockRepositoryImpl,
    ): StockRepository

    @Binds
    @Singleton
    abstract fun bindStockRemoteDataSource(
        impl: StockSupabaseDataSourceImpl,
    ): StockRemoteDataSource
}