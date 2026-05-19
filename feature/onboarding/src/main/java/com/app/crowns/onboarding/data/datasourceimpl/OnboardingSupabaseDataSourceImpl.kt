package com.app.crowns.onboarding.data.datasourceimpl

import com.app.crowns.data.company.entity.CompanyEntity
import com.app.crowns.data.company.mapper.toDomain
import com.app.crowns.data.company.mapper.toEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.onboarding.data.datasource.OnboardingSupabaseDataSource
import javax.inject.Inject
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class OnboardingSupabaseDataSourceImpl @Inject constructor(
    private val supabase: SupabaseClient,
) : OnboardingSupabaseDataSource {
    override suspend fun saveCompany(
        companyInfo: CompanyInfo,
        logoUrl: String?
    ): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id ?: error("User not authenticated")
        val entity = companyInfo.toEntity(
            userId = userId,
            logoUrl = logoUrl,
        )

        supabase.from("companies").insert(entity)
    }

    override suspend fun uploadLogo(
        imageBytes: ByteArray
    ): Result<String> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")
        
        // Usar un timestamp en el nombre del archivo para forzar la actualización de la URL y evitar caché
        val timestamp = System.currentTimeMillis()
        val path = "$userId/logo_$timestamp.jpg"

        supabase.storage
            .from("business-logos")
            .upload(path, imageBytes) {
                upsert = true
            }

        supabase.storage
            .from("business-logos")
            .publicUrl(path)
    }

    override suspend fun getCompany(): Result<CompanyInfo?> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")

        supabase.from("companies")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<CompanyEntity>()
            ?.toDomain()
    }
}