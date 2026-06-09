package com.app.crowns.domain.auth.usecase

import com.app.crowns.common.Result
import com.app.crowns.domain.auth.model.AuthError
import com.app.crowns.domain.auth.repository.AuthRepository
import javax.inject.Inject

class CheckSubscriptionUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit, AuthError> = repository.checkSubscription()
}
