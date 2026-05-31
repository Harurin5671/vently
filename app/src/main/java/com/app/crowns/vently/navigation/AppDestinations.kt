package com.app.crowns.vently.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AppDestination : NavKey {
    @Serializable
    data object Splash : AppDestination

    @Serializable
    data object Auth : AppDestination

    @Serializable
    data object Onboarding : AppDestination

    @Serializable
    data object Home : AppDestination

    @Serializable
    data class AddProduct(val id: String) : AppDestination

    @Serializable
    data class CreateInvoice(val isFactura: Boolean, val id: String) : AppDestination

    @Serializable
    data object InvoicePreview : AppDestination
}

@Serializable
sealed interface HomeTab : NavKey {
    @Serializable
    data object Dashboard : HomeTab

    @Serializable
    data object Stock : HomeTab

    @Serializable
    data object History : HomeTab

    @Serializable
    data object Settings : HomeTab
}