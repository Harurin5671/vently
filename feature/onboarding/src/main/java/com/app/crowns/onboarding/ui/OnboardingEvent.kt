package com.app.crowns.onboarding.ui

import android.net.Uri

sealed class OnboardingEvent {
    data object NextPage: OnboardingEvent()
    data object PreviousPage : OnboardingEvent()
    data object Finish : OnboardingEvent()
    data class OnChangedRuc(val ruc: String) : OnboardingEvent()
    data class OnLogoSelected(val uri: Uri) : OnboardingEvent()
}

sealed class OnboardingNavigationEvent {
    data object NavigateToHome : OnboardingNavigationEvent()
}