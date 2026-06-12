package com.app.crowns.auth.data.repository

import com.app.crowns.auth.data.mapper.toAuthError
import com.app.crowns.auth.data.mapper.toUser
import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.model.SubscriptionStatus
import com.app.crowns.domain.auth.model.User
import com.app.crowns.domain.auth.repository.AuthRepository
import com.app.crowns.common.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.status.SessionStatus
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepository {

    companion object {
        private const val SUBSCRIPTION_CACHE_TTL_MS = 5 * 60 * 1000L // 5 minutos
    }
    override suspend fun loginWithGoogle(): Result<User, AuthError> = try {
        supabase.auth.signInWith(Google)
        val session = supabase.auth.currentSessionOrNull()
            ?: return Result.Failure(AuthError.InvalidCredentials)
        Result.Success(session.toUser())
    } catch (e: Exception) {
        Result.Failure(e.toAuthError())
    }

    override suspend fun logout(): Result<Unit, AuthError> = try {
        supabase.auth.signOut()
        subscriptionCache = null
        subscriptionCacheTime = 0L
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Failure(e.toAuthError())
    }

    override suspend fun deleteAccount(): Result<Unit, AuthError> = try {
        supabase.functions.invoke("delete-account")
        supabase.auth.signOut()
        Result.Success(Unit)
    } catch (e: Exception) {
        Result.Failure(e.toAuthError())
    }

    override suspend fun getCurrentUser(): User? =
        supabase.auth.currentSessionOrNull()?.toUser()

    override suspend fun isSessionActive(): Boolean {
        // Al tener alwaysAutoRefresh = true, currentSessionOrNull() 
        // gestionará la carga y el refresco automáticamente.
        return supabase.auth.currentSessionOrNull() != null
    }

    override fun observeSession(): Flow<Boolean> {
        return supabase.auth.sessionStatus.map { status ->
            when (status) {
                is SessionStatus.Authenticated -> true
                is SessionStatus.NotAuthenticated -> false
                else -> null
            }
        }.filterNotNull()
    }

    private var subscriptionCache: Result<Unit, AuthError>? = null
    private var subscriptionCacheTime: Long = 0L

    override suspend fun checkSubscription(): Result<Unit, AuthError> {
        val now = System.currentTimeMillis()
        if (subscriptionCache is Result.Success && now - subscriptionCacheTime < SUBSCRIPTION_CACHE_TTL_MS) {
            return subscriptionCache!!
        }
        return try {
            val userId = supabase.auth.currentSessionOrNull()?.user?.id
                ?: return Result.Failure(AuthError.SubscriptionNotActive)

            val rawJson = supabase.postgrest
                .from("subscriptions")
                .select(Columns.list("status")) {
                    filter { eq("user_id", userId) }
                    limit(1)
                }
                .data

            val status = Json.parseToJsonElement(rawJson)
                .jsonArray
                .firstOrNull()
                ?.jsonObject
                ?.get("status")
                ?.jsonPrimitive
                ?.content

            val subStatus = status?.let { raw ->
                SubscriptionStatus.entries.find { it.name == raw.uppercase() }
            }
            val result = if (subStatus?.isAllowed == true) {
                Result.Success(Unit)
            } else {
                Result.Failure(AuthError.SubscriptionNotActive)
            }
            subscriptionCache = result
            subscriptionCacheTime = now
            result
        } catch (e: Exception) {
            Result.Failure(e.toAuthError())
        }
    }

}
