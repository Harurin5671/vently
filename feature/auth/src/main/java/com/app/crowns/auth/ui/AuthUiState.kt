package com.app.crowns.auth.ui

import com.app.crowns.auth.domain.model.AuthError
import com.app.crowns.auth.domain.model.User

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: AuthError? = null
)