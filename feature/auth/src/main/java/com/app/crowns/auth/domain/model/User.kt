package com.app.crowns.auth.domain.model

data class User (
    val id: String,
    val email: String,
    val displayName: String? = null,
    val avatarUrl: String? = null,
)