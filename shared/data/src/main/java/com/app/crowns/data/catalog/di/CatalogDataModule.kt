package com.app.crowns.data.catalog.di

import com.app.crowns.data.catalog.datasource.CatalogDataSource
import com.app.crowns.data.catalog.datasource.CatalogSupabaseDataSourceImpl
import com.app.crowns.data.catalog.repository.CatalogRepositoryImpl
import com.app.crowns.domain.catalog.repository.CatalogRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CatalogDataModule {

    @Binds
    @Singleton
    abstract fun bindCatalogDataSource(
        impl: CatalogSupabaseDataSourceImpl
    ): CatalogDataSource

    @Binds
    @Singleton
    abstract fun bindCatalogRepository(
        impl: CatalogRepositoryImpl
    ): CatalogRepository
}