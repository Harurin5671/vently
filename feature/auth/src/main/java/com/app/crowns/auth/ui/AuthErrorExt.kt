package com.app.crowns.auth.ui

import com.app.crowns.domain.auth.model.AuthError

fun AuthError.toMessage(): String = when (this) {
    is AuthError.NetworkError -> "Sin conexión a internet"
    is AuthError.InvalidCredentials -> "Credenciales inválidas"
    is AuthError.UserNotFound -> "Usuario no encontrado"
    is AuthError.EmailAlreadyInUse -> "El correo ya está en uso"
    is AuthError.SubscriptionNotActive -> "Suscripción no activa"
    is AuthError.Unknown -> message
}
