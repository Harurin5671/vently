package com.app.crowns.data.product.di

import com.app.crowns.data.product.datasource.ProductRemoteDataSource
import com.app.crowns.data.product.datasource.ProductSupabaseDataSourceImpl
import com.app.crowns.data.product.repository.ProductRepositoryImpl
import com.app.crowns.domain.product.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ProductDataModule {

    @Binds
    @Singleton
    abstract fun bindProductRemoteDataSource(
        impl: ProductSupabaseDataSourceImpl
    ): ProductRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository
}
