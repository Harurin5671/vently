package com.app.crowns.network.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import javax.inject.Qualifier
import javax.inject.Singleton
import com.app.crowns.network.BuildConfig
import io.github.jan.supabase.compose.auth.ComposeAuth
import io.github.jan.supabase.compose.auth.googleNativeLogin
import io.github.jan.supabase.storage.Storage
import com.russhwolf.settings.SharedPreferencesSettings
import io.github.jan.supabase.auth.SettingsSessionManager
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.content.SharedPreferences

@Module
@InstallIn(SingletonComponent::class)
object SupabaseModule {

    @Provides
    @Singleton
    @Suppress("DEPRECATION")
    fun provideEncryptedSharedPreferences(
        @ApplicationContext context: Context
    ): SharedPreferences {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        return EncryptedSharedPreferences.create(
            context,
            "secure_vently_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    @Singleton
    fun provideSupabaseClient(
        @ApplicationContext context: Context,
        encryptedSharedPreferences: SharedPreferences
    ): SupabaseClient = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY,
    ) {
        install(Auth) {
            scheme = "com.app.crowns.vently"
            host = "callback"
            sessionManager = SettingsSessionManager(SharedPreferencesSettings(encryptedSharedPreferences))
            alwaysAutoRefresh = true
        }
        install(Postgrest)
        install(Storage)
        install(ComposeAuth) {
            googleNativeLogin(serverClientId = BuildConfig.GOOGLE_CLIENT_ID)
        }
    }
}

@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class SupabaseUrl

@Qualifier @Retention(AnnotationRetention.BINARY)
annotation class SupabaseKey