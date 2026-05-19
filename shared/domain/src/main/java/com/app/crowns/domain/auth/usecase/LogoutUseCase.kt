package com.app.crowns.domain.auth.usecase

import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.repository.AuthRepository
import com.app.crowns.common.Result
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): com.app.crowns.common.Result<Unit, AuthError> = repository.logout()
}