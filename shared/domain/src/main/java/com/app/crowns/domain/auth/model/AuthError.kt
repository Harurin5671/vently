package com.app.crowns.domain.auth.model

sealed class AuthError {
    data object InvalidCredentials: AuthError()
    data object EmailAlreadyInUse: AuthError()
    data object NetworkError: AuthError()
    data object UserNotFound: AuthError()
    data class Unknown(val message: String): AuthError()
}