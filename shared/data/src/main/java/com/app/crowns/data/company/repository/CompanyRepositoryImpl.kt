package com.app.crowns.data.company.repository

import android.util.Log
import com.app.crowns.data.company.dto.CompanyInfoDto
import com.app.crowns.data.company.entity.CompanyEntity
import com.app.crowns.data.company.mapper.toDomain
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.repository.CompanyRepository
import com.app.crowns.network.KtorHttpClient
import com.app.crowns.network.NetworkResult
import com.app.crowns.network.di.ApiBaseUrl
import com.app.crowns.network.map
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
    private val httpClient: KtorHttpClient,
    @param:ApiBaseUrl private val baseUrl: String,
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

    override suspend fun getCompanyInfo(ruc: String): NetworkResult<CompanyInfo> {
        Log.d("COMPANY_REPO", "Iniciando getCompanyInfo para RUC: $ruc")
        Log.d("COMPANY_REPO", "URL base: $baseUrl")
        Log.d("COMPANY_REPO", "URL completa: ${baseUrl}get-ruc")

        val result = httpClient.get(
            url = "${baseUrl}get-ruc",
            deserializer = CompanyInfoDto.serializer(),
            queryParams = mapOf("ruc" to ruc),
        )

        Log.d("COMPANY_REPO", "Resultado de red crudo: $result")

        return result.map { dto ->
            val domain = dto.toDomain()
            Log.d("COMPANY_REPO", "DTO mapeado exitosamente a dominio: $domain")
            domain
        }
    }
}
