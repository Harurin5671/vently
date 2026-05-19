package com.app.crowns.domain.auth.repository

import com.app.crowns.common.Result
import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithGoogle(): Result<User, AuthError>
    suspend fun logout(): Result<Unit, AuthError>
    suspend fun getCurrentUser(): User?
    suspend fun isSessionActive(): Boolean
    fun observeSession(): Flow<Boolean>
}
