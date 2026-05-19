package com.app.crowns.domain.auth.usecase

import com.app.crowns.domain.auth.model.User
import com.app.crowns.domain.auth.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): User? = repository.getCurrentUser()
}