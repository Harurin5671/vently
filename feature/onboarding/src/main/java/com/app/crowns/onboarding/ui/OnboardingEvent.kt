package com.app.crowns.onboarding.ui

sealed class OnboardingEvent {
    data object NextPage: OnboardingEvent()
    data object PreviousPage : OnboardingEvent()
    data object Finish : OnboardingEvent()
    data class OnChangedRuc(val ruc: String) : OnboardingEvent()
}

sealed class OnboardingNavigationEvent {
    data object NavigateToHome : OnboardingNavigationEvent()
}