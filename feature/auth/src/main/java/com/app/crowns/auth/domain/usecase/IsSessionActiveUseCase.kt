package com.app.crowns.auth.domain.usecase

import com.app.crowns.auth.domain.repository.AuthRepository
import javax.inject.Inject

class IsSessionActiveUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.isSessionActive()
}