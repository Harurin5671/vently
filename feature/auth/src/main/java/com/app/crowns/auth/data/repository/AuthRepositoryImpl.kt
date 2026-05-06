package com.app.crowns.auth.data.repository

import com.app.crowns.auth.data.mapper.toAuthError
import com.app.crowns.auth.data.mapper.toUser
import com.app.crowns.auth.domain.model.AuthError
import com.app.crowns.auth.domain.model.User
import com.app.crowns.auth.domain.repository.AuthRepository
import com.app.crowns.common.Result
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
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

    override suspend fun getCurrentUser(): User? =
        supabase.auth.currentSessionOrNull()?.toUser()

    override suspend fun isSessionActive(): Boolean =
        supabase.auth.currentSessionOrNull() != null
}