package com.app.crowns.auth.domain.usecase

import com.app.crowns.auth.domain.model.AuthError
import com.app.crowns.auth.domain.repository.AuthRepository
import com.app.crowns.common.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit, AuthError> = repository.logout()
}