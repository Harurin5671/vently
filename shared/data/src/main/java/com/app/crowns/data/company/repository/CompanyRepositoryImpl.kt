package com.app.crowns.data.company.repository

import android.util.Log
import com.app.crowns.data.company.dto.CompanyInfoDto
import com.app.crowns.data.company.entity.CompanyEntity
import com.app.crowns.data.company.entity.DocumentSeriesEntity
import com.app.crowns.data.company.mapper.toDomain
import com.app.crowns.data.company.mapper.toEntity
import com.app.crowns.data.customer.dto.PersonInfoDto
import com.app.crowns.data.customer.dto.toDomain
import com.app.crowns.domain.catalog.entities.ElectronicDocumentTypeEntity
import com.app.crowns.domain.company.model.CompanyInfo
import com.app.crowns.domain.company.model.DocumentSeries
import com.app.crowns.domain.customer.model.PersonInfo
import com.app.crowns.data.security.CryptoManager
import com.app.crowns.domain.company.repository.CompanyRepository
import com.app.crowns.network.KtorHttpClient
import com.app.crowns.network.NetworkResult
import com.app.crowns.network.di.ApiBaseUrl
import com.app.crowns.network.map
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient,
    private val httpClient: KtorHttpClient,
    @param:ApiBaseUrl private val baseUrl: String,
    private val crypto: CryptoManager,
) : CompanyRepository {
    override suspend fun getCompany(): Result<CompanyInfo?> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: return Result.success(null)

        supabase.from("companies")
            .select {
                filter { eq("user_id", userId) }
            }
            .decodeSingleOrNull<CompanyEntity>()
            ?.let { entity ->
                entity.copy(
                    certificatePassword = entity.certificatePassword?.let { crypto.decrypt(it) },
                    solUsername = entity.solUsername?.let { crypto.decrypt(it) },
                    solPassword = entity.solPassword?.let { crypto.decrypt(it) },
                ).toDomain()
            }
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

    override suspend fun getPersonInfo(dni: String): NetworkResult<PersonInfo> {
        Log.d("COMPANY_REPO", "Iniciando getPersonInfo para DNI: $dni")
        Log.d("COMPANY_REPO", "URL completa: ${baseUrl}get-dni")

        val result = httpClient.get(
            url = "${baseUrl}get-dni",
            deserializer = PersonInfoDto.serializer(),
            queryParams = mapOf("dni" to dni),
        )

        return result.map { it.toDomain() }
    }

    override suspend fun updateCertificate(base64: String): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("Usuario no autenticado")

        supabase.from("companies").update(
            {
                set("certificate_base64", base64)
            }
        ) {
            filter {
                eq("user_id", userId)
            }
        }
    }

    override suspend fun updateCertificatePassword(password: String): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("Usuario no autenticado")

        supabase.from("companies").update(
            {
                set("certificate_password", crypto.encrypt(password))
            }
        ) {
            filter {
                eq("user_id", userId)
            }
        }
    }

    override suspend fun getDocumentSeries(companyId: String): Result<List<DocumentSeries>> = runCatching {
        supabase.from("document_series")
            .select {
                filter { eq("company_id", companyId) }
            }
            .decodeList<DocumentSeriesEntity>()
            .map { it.toDomain() }
    }

    override suspend fun updateSolCredentials(username: String, password: String): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("Usuario no autenticado")

        supabase.from("companies").update(
            {
                set("sol_username", crypto.encrypt(username))
                set("sol_password", crypto.encrypt(password))
            }
        ) {
            filter { eq("user_id", userId) }
        }
    }

    override suspend fun updateDocumentSeries(series: DocumentSeries): Result<Unit> = runCatching {
        val entity = series.toEntity()
        
        if (entity.id != null) {
            // Si tenemos el ID, actualizamos directamente por ID para mayor seguridad
            supabase.from("document_series")
                .update(entity) {
                    filter { eq("id", entity.id) }
                }
        } else {
            // Si no hay ID, usamos upsert basado en la restricción única de compañía y tipo de documento
            supabase.from("document_series")
                .upsert(entity) {
                    onConflict = "company_id,document_type"
                }
        }
    }

    override suspend fun updateCompany(company: CompanyInfo): Result<Unit> = runCatching {
        Log.d("COMPANY_REPO", "Iniciando updateCompany...")
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: throw Exception("Usuario no autenticado")

        // Para evitar problemas con el ID al actualizar, usamos un mapa de campos específicos
        // o nos aseguramos de que el filtro sea correcto.
        val entity = company.toEntity(userId)
        
        Log.d("COMPANY_REPO", "Actualizando compañía para usuario: $userId")
        Log.d("COMPANY_REPO", "Datos a actualizar: tradeName=${entity.tradeName}, address=${entity.address}, logoUrl=${entity.logoUrl}")

        supabase.from("companies").update(
            {
                entity.tradeName?.let { set("trade_name", it) }
                entity.address?.let { set("address", it) }
                entity.logoUrl?.let { set("logo_url", it) }
                set("business_name", entity.businessName)
                set("ruc", entity.ruc)
            }
        ) {
            filter { eq("user_id", userId) }
        }
        Log.d("COMPANY_REPO", "updateCompany finalizado exitosamente")
    }

    override suspend fun saveCompany(
        companyInfo: CompanyInfo,
        logoUrl: String?
    ): Result<Unit> = runCatching {
        val userId = supabase.auth.currentUserOrNull()?.id ?: error("User not authenticated")
        val entity = companyInfo.toEntity(
            userId = userId,
            logoUrl = logoUrl,
        )

        // Insertar la compañía y obtener el ID generado
        val insertedCompany = supabase.from("companies").insert(entity) {
            select()
        }.decodeSingle<CompanyEntity>()

        val companyId = insertedCompany.id ?: error("Error: No se pudo obtener el ID de la compañía")

        // Crear las series por defecto para Facturas (01) y Boletas (03)
        val series = listOf(
            DocumentSeriesEntity(
                companyId = companyId,
                documentType = ElectronicDocumentTypeEntity.FACTURA,
                serie = "F001",
                lastCorrelative = 0,
                isActive = true
            ),
            DocumentSeriesEntity(
                companyId = companyId,
                documentType = ElectronicDocumentTypeEntity.BOLETA,
                serie = "B001",
                lastCorrelative = 0,
                isActive = true
            )
        )

        supabase.from("document_series").insert(series)
    }

    override suspend fun uploadLogo(
        imageBytes: ByteArray
    ): Result<String> = runCatching {
        Log.d("COMPANY_REPO", "Iniciando uploadLogo...")
        val userId = supabase.auth.currentUserOrNull()?.id
            ?: error("User not authenticated")

        val bucket = supabase.storage.from("business-logos")

        // 1. Listar y borrar TODOS los archivos existentes para este usuario
        try {
            Log.d("COMPANY_REPO", "Listando archivos para usuario: $userId")
            val existingFiles = bucket.list(userId)
            Log.d("COMPANY_REPO", "Archivos encontrados: ${existingFiles.size}")
            if (existingFiles.isNotEmpty()) {
                val pathsToDelete = existingFiles.map { file ->
                    if (file.name.contains(userId)) file.name else "$userId/${file.name}"
                }
                Log.d("COMPANY_REPO", "Borrando archivos: $pathsToDelete")
                bucket.delete(pathsToDelete)
                Log.d("COMPANY_REPO", "Archivos borrados")
            }
        } catch (e: Exception) {
            Log.w("COMPANY_REPO", "Error al limpiar logos anteriores: ${e.message}")
        }

        // 2. Subir con nombre fijo
        val path = "$userId/logo.jpg"
        Log.d("COMPANY_REPO", "Subiendo nuevo logo a: $path")

        bucket.upload(path, imageBytes) {
            upsert = true
        }
        Log.d("COMPANY_REPO", "Subida completada")

        // 3. Devolver la URL con un timestamp para forzar la recarga en la UI
        val publicUrl = bucket.publicUrl(path)
        val finalUrl = "$publicUrl?t=${System.currentTimeMillis()}"
        Log.d("COMPANY_REPO", "URL final generada: $finalUrl")
        finalUrl
    }
}
