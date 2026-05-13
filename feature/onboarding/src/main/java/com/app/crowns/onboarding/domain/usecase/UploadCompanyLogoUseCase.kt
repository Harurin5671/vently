package com.app.crowns.onboarding.domain.usecase

import com.app.crowns.onboarding.domain.repository.OnboardingRepository
import javax.inject.Inject

class UploadCompanyLogoUseCase @Inject constructor(
    private val repository: OnboardingRepository,
) {
    suspend operator fun invoke(
        imageBytes: ByteArray,
    ): Result<String> = repository.uploadLogo(imageBytes)
}