package com.app.crowns.invoice.di

import com.app.crowns.invoice.domain.store.InvoiceDraftStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object InvoiceModule {

    @Provides
    @Singleton
    fun provideInvoiceDraftStore(): InvoiceDraftStore = InvoiceDraftStore()
}