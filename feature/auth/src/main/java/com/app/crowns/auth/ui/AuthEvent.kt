package com.app.crowns.auth.ui

import io.github.jan.supabase.compose.auth.composable.NativeSignInResult

sealed class AuthEvent {
    data object Logout : AuthEvent()
    data object SignInWithGoogle : AuthEvent()
    data class HandleGoogleResult(val result: NativeSignInResult) : AuthEvent()
}

sealed class AuthNavigationEvent {
    data object NavigateToOnboarding : AuthNavigationEvent()
    data object NavigateToHome : AuthNavigationEvent()
}