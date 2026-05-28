package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class UpdateCertificatePasswordUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(password: String): Result<Unit> {
        return repository.updateCertificatePassword(password)
    }
}
