package com.app.crowns.domain.auth.model

data class Subscription(
    val userId: String,
    val status: SubscriptionStatus,
    val plan: String,
)

enum class SubscriptionStatus {
    ACTIVE, TRIAL, SUSPENDED, PENDING;

    val isAllowed: Boolean get() = this == ACTIVE || this == TRIAL
}
