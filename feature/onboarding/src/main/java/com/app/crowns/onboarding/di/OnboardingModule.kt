package com.app.crowns.onboarding.di

import com.app.crowns.onboarding.data.datasource.OnboardingRemoteDataSource
import com.app.crowns.onboarding.data.datasource.OnboardingSupabaseDataSource
import com.app.crowns.onboarding.data.datasourceimpl.OnboardingRemoteDataSourceImpl
import com.app.crowns.onboarding.data.datasourceimpl.OnboardingSupabaseDataSourceImpl
import com.app.crowns.onboarding.data.repository.OnboardingRepositoryImpl
import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OnboardingModule {

    @Binds
    @Singleton
    abstract fun bindOnboardingRepository(
        impl: OnboardingRepositoryImpl,
    ): OnboardingRepository

    @Binds
    @Singleton
    abstract fun bindOnboardingRemoteDataSource(
        impl: OnboardingRemoteDataSourceImpl,
    ): OnboardingRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindOnboardingSupabaseDataSource(
        impl: OnboardingSupabaseDataSourceImpl,
    ): OnboardingSupabaseDataSource
}