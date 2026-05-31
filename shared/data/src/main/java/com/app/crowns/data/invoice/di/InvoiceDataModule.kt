package com.app.crowns.data.invoice.di

import com.app.crowns.data.invoice.datasource.InvoiceSupabaseDataSource
import com.app.crowns.data.invoice.datasource.InvoiceSupabaseDataSourceImpl
import com.app.crowns.data.invoice.repository.InvoiceRepositoryImpl
import com.app.crowns.domain.invoice.repository.InvoiceRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InvoiceDataModule {

    @Binds
    @Singleton
    abstract fun bindInvoiceSupabaseDataSource(
        impl: InvoiceSupabaseDataSourceImpl
    ): InvoiceSupabaseDataSource

    @Binds
    @Singleton
    abstract fun bindInvoiceRepository(
        impl: InvoiceRepositoryImpl
    ): InvoiceRepository
}
