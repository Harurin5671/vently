package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class UpdateSolCredentialsUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(username: String, password: String): Result<Unit> {
        return repository.updateSolCredentials(username, password)
    }
}
