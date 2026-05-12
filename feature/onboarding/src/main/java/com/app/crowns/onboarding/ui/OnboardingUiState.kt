package com.app.crowns.onboarding.ui

import android.net.Uri
import com.app.crowns.onboarding.domain.model.CompanyInfo

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 2,
    val ruc: String = "",
    val isValid: Boolean =  false,
    val isSearching: Boolean = false,
    val companyInfo: CompanyInfo? = null,
    val searchError: String? = null,
    val logoUri: Uri? = null,
)

val OnboardingUiState.isFirstPage get() = currentPage == 0
val OnboardingUiState.isLastPage get() = currentPage == totalPages - 1