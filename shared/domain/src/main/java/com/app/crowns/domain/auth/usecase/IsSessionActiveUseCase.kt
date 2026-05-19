package com.app.crowns.domain.auth.usecase

import com.app.crowns.domain.auth.repository.AuthRepository
import javax.inject.Inject

class IsSessionActiveUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Boolean = repository.isSessionActive()
}