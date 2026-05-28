package com.app.crowns.domain.company.usecase

import com.app.crowns.domain.company.repository.CompanyRepository
import javax.inject.Inject

class UpdateCertificateUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(base64: String): Result<Unit> {
        return repository.updateCertificate(base64)
    }
}
