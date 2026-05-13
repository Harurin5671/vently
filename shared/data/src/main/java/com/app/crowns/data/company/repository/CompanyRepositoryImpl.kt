package com.app.crowns.data.company.repository

import com.app.crowns.data.company.entity.CompanyEntity
import com.app.crowns.data.company.mapper.toDomain
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.repository.CompanyRepository
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : CompanyRepository {
    override suspend fun getCompany(): Result<CompanyInfo?> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.success(null)

        supabase.from("companies")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<CompanyEntity>()
            ?.toDomain()
    }
}
