package com.app.crowns.network.di

import com.app.crowns.network.KtorHttpClient
import com.app.crowns.network.client.HttpClient
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HttpClientBindingModule {

    @Binds
    @Singleton
    abstract fun bindHttpClient(
        impl: KtorHttpClient
    ): HttpClient
}