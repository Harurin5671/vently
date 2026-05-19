package com.app.crowns.auth.data.mapper

import com.app.crowns.domain.auth.model.AuthError
import io.github.jan.supabase.exceptions.RestException

fun Throwable.toAuthError(): AuthError = when (this) {
    is RestException -> when (error) {
        "invalid_grant"       -> AuthError.InvalidCredentials
        "user_already_exists" -> AuthError.EmailAlreadyInUse
        "user_not_found"      -> AuthError.UserNotFound
        else                  -> AuthError.Unknown(message ?: "Unknown error")
    }
    else -> if (message?.contains("network", ignoreCase = true) == true)
        AuthError.NetworkError
    else
        AuthError.Unknown(message ?: "Unknown error")
}