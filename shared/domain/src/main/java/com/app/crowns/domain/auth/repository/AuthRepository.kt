package com.app.crowns.domain.auth.repository

import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.model.User
import com.app.crowns.common.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun loginWithGoogle(): com.app.crowns.common.Result<User, AuthError>
    suspend fun logout(): com.app.crowns.common.Result<Unit, AuthError>
    suspend fun getCurrentUser(): User?
    suspend fun isSessionActive(): Boolean
    fun observeSession(): Flow<Boolean>
}
