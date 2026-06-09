package com.app.crowns.auth.data.repository

import com.app.crowns.auth.data.mapper.toAuthError
import com.app.crowns.auth.data.mapper.toUser
import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.model.Subscription
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
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val supabase: SupabaseClient
) : AuthRepository {
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

    override suspend fun checkSubscription(): Result<Unit, AuthError> = try {
        val row = supabase.postgrest
            .from("subscriptions")
            .select(Columns.list("status")) {
                limit(1)
            }
            .decodeSingleOrNull<SubscriptionRow>()

        if (row != null && SubscriptionStatus.valueOf(row.status.uppercase()).isAllowed) {
            Result.Success(Unit)
        } else {
            Result.Failure(AuthError.SubscriptionNotActive)
        }
    } catch (e: Exception) {
        Result.Failure(e.toAuthError())
    }

    @Serializable
    private data class SubscriptionRow(@SerialName("status") val status: String)
}
