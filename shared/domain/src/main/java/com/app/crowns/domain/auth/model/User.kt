package com.app.crowns.domain.auth.model

data class User (
    val id: String,
    val email: String,
    val displayName: String? = null,
    val avatarUrl: String? = null,
)