package com.app.crowns.data.product.di

import com.app.crowns.data.product.datasource.IgvTypeSupabaseDataSource
import com.app.crowns.data.product.datasource.IgvTypeSupabaseDataSourceImpl
import com.app.crowns.data.product.repository.IgvTypeRepositoryImpl
import com.app.crowns.domain.product.repository.IgvTypeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IgvTypeDataModule {

    @Binds
    @Singleton
    abstract fun bindIgvTypeSupabaseDataSource(
        impl: IgvTypeSupabaseDataSourceImpl
    ): IgvTypeSupabaseDataSource

    @Binds
    @Singleton
    abstract fun bindIgvTypeRepository(
        impl: IgvTypeRepositoryImpl
    ): IgvTypeRepository
}