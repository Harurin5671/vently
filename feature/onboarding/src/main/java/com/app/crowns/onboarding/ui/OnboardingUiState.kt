package com.app.crowns.onboarding.ui

data class OnboardingUiState(
    val currentPage: Int = 0,
    val totalPages: Int = 3,
)

val OnboardingUiState.isFirstPage get() = currentPage == 0
val OnboardingUiState.isLastPage get() = currentPage == totalPages - 1