package com.app.crowns.auth.domain.usecase

import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.model.User
import com.app.crowns.domain.auth.repository.AuthRepository
import com.app.crowns.common.Result
import javax.inject.Inject

class LoginWithGoogleUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<User, AuthError> = repository.loginWithGoogle()
}