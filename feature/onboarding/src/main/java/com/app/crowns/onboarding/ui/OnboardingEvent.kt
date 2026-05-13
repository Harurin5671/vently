package com.app.crowns.onboarding.ui

import android.net.Uri

sealed class OnboardingEvent {
    data object NextPage: OnboardingEvent()
    data object PreviousPage : OnboardingEvent()
    data class Finish(val logoBytes: ByteArray? = null) : OnboardingEvent() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Finish) return false
            return logoBytes.contentEquals(other.logoBytes)
        }

        override fun hashCode(): Int {
            return logoBytes.contentHashCode()
        }
    }

    data class OnChangedRuc(val ruc: String) : OnboardingEvent()
    data class OnLogoSelected(val uri: Uri) : OnboardingEvent()
}

sealed class OnboardingNavigationEvent {
    data object NavigateToHome : OnboardingNavigationEvent()
}