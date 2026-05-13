package com.app.crowns.auth.domain.repository

import com.app.crowns.auth.domain.model.AuthError
import com.app.crowns.auth.domain.model.User
import com.app.crowns.common.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithGoogle(): Result<User, AuthError>
    suspend fun logout(): Result<Unit, AuthError>
    suspend fun getCurrentUser(): User?
    suspend fun isSessionActive(): Boolean
    fun observeSession(): Flow<Boolean>
}
