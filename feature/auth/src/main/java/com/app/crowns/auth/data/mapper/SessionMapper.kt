package com.app.crowns.auth.data.mapper

import com.app.crowns.auth.domain.model.User
import io.github.jan.supabase.auth.user.UserSession

fun UserSession.toUser(): User = User(
    id = user?.id ?: "",
    email = user?.email ?: "",
    displayName = user?.userMetadata
        ?.get("full_name")
        ?.toString()
        ?.trim('"'),
    avatarUrl = user?.userMetadata
        ?.get("avatar_url")
        ?.toString()
        ?.trim('"')
)