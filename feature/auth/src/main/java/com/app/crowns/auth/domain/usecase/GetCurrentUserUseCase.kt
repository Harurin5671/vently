package com.app.crowns.auth.domain.usecase

import com.app.crowns.auth.domain.model.User
import com.app.crowns.auth.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? = repository.getCurrentUser()
}