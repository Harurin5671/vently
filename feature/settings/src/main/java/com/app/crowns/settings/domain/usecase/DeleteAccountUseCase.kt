package com.app.crowns.settings.domain.usecase

import com.app.crowns.domain.auth.repository.AuthRepository
import javax.inject.Inject

class DeleteAccountUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): kotlin.Result<Unit> = runCatching {
        val result = repository.deleteAccount()
        if (result is com.app.crowns.common.Result.Failure) error(result.error.toString())
    }
}
