package com.app.crowns.onboarding.ui

sealed class OnboardingEvent {
    data object NextPage: OnboardingEvent()
    data object PreviousPage : OnboardingEvent()
    data object Finish : OnboardingEvent()
}

sealed class OnboardingNavigationEvent {
    data object NavigateToHome : OnboardingNavigationEvent()
}